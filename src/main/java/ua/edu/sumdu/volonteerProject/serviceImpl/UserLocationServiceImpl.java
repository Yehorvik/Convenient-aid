package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.UserLocation;
import ua.edu.sumdu.volonteerProject.repos.CitiesRepo;
import ua.edu.sumdu.volonteerProject.repos.UserLocationRepository;
import ua.edu.sumdu.volonteerProject.services.UserLocationService;

import javax.transaction.Transactional;

import static java.lang.Math.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserLocationServiceImpl implements UserLocationService {

    private static final double SOME_REGULAR_CONSTANT_KOEF = 25;
    private static final double CONSTANT_FOR_EXP = 90000;
    private static final double LAT_TO_KM = 110.573;
    private static final double LON_TO_KM = 111.320;

   // private final ModelMapper modelMapper;
    private final UserLocationRepository userLocationRepository;

    private final CitiesRepo citiesRepo;

    private double computeInnerKoefs(UserLocation currentUser, List<UserLocation> allUsers,double cityArea, int amount,  int from, int to){
        double retValue = 0;

        CoordinatesAndK coordinatesAndK = new CoordinatesAndK(currentUser.getLocationCoordinates(), 0);
        final double SQUARE_KOEF = CONSTANT_FOR_EXP * (double) amount / cityArea;
        for (int i = from; i < to; i++) {
            LocationCoordinates innerLC = allUsers.get(i).getLocationCoordinates();
            LocationCoordinates outerLC = coordinatesAndK.locationCoordinates;
            //latitude

            double x = (toRadians(innerLC.getLatitude())) - toRadians(outerLC.getLatitude());
            //longitude
            double y = (toRadians(innerLC.getLongitude() - outerLC.getLongitude()))*cos((innerLC.getLatitude()+outerLC.getLatitude())*PI/2/180);
            retValue += exp(SQUARE_KOEF * -sqrt(
                    x*x + y*y
            ));
        }
        return retValue;
    }

    private List<CoordinatesAndK> computeKoefsMT(List<UserLocation> userLocations,City city, int amount, int from, int to) {

        List<CoordinatesAndK> coordinatesAndKS = new ArrayList<>();
        for (int i= from; i<to; i++) {
            List<CompletableFuture<Double>> completableFutures = new ArrayList<>();
            CoordinatesAndK coordinatesAndK = new CoordinatesAndK(userLocations.get(i).getLocationCoordinates(), 0);
            int finalI = i;
            completableFutures.add(  CompletableFuture.supplyAsync(()-> this.computeInnerKoefs(userLocations.get(finalI), userLocations,city.getArea(),amount, 0,userLocations.size()/4)).exceptionally((e) -> {log.error("exception in  computeKoefsMT",e); return null;}));
            completableFutures.add(CompletableFuture.supplyAsync(()-> this.computeInnerKoefs(userLocations.get(finalI), userLocations,city.getArea(),amount, userLocations.size()/4,userLocations.size()/2)).exceptionally((e) -> {log.error("exception in  computeKoefsMT",e); return null;}));
            completableFutures.add(CompletableFuture.supplyAsync(()-> this.computeInnerKoefs(userLocations.get(finalI), userLocations,city.getArea(),amount, userLocations.size()/2,userLocations.size()*3/4)).exceptionally((e) -> {log.error("exception in  computeKoefsMT",e); return null;}));
            completableFutures.add(CompletableFuture.
                    supplyAsync(()-> this.computeInnerKoefs(userLocations.get(finalI), userLocations,city.getArea(),amount, userLocations.size()*3/4,userLocations.size())).exceptionally((e) -> {log.error("exception in  computeKoefsMT",e); return null;}));
            for(CompletableFuture<Double> k : completableFutures){
                try {
                    coordinatesAndK.k+=k.get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            coordinatesAndKS.add(coordinatesAndK);
        }
       // System.out.println(coordinatesAndKS);
        return coordinatesAndKS;
    }

    private List<CoordinatesAndK> computeKoefs(List<UserLocation> userLocations,City city, int amount) throws ExecutionException, InterruptedException {
        List<CompletableFuture> completableFutures = new ArrayList<>();
        /*for(int i = 1 ; i<= CORES && userLocations.size()>CORES; i++){
            int finalI = i;
        }
        */
            completableFutures.add(CompletableFuture.supplyAsync(() -> this.computeKoefsMT(userLocations, city, amount, 0, userLocations.size() / 4)));
            completableFutures.add(CompletableFuture.supplyAsync(() -> this.computeKoefsMT(userLocations, city, amount, userLocations.size() / 4, userLocations.size() / 2)));
            completableFutures.add(CompletableFuture.supplyAsync(() -> this.computeKoefsMT(userLocations, city, amount, userLocations.size() / 2, (userLocations.size() * 3) / 4)));
            completableFutures.add(CompletableFuture.supplyAsync(() -> this.computeKoefsMT(userLocations, city, amount, ((userLocations.size() * 3) / 4), userLocations.size())));
        List<CoordinatesAndK> list = new ArrayList<>();
        for (CompletableFuture t:
             completableFutures) {
            list.addAll(
            (List<CoordinatesAndK>)t.get());
        }
        System.out.println(list);
        return list;
    }

    @Override
    public List<LocationCoordinates> getCoordinates(City city) {
        if(city == null){
            throw new NullPointerException("city cant be null");
        }
        return userLocationRepository.findByCityName(city.getName()).stream().map(e->{return e.getLocationCoordinates();}).collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Transactional
    public List<LocationCoordinates> getFittedCoordinatesByLocation(CityDTO city, int amountOfLocations) throws IllegalAccessException {
        if(amountOfLocations <=0){
            throw new IllegalAccessException("amount of locations cant be negative");
        }
        City authorizedCity = citiesRepo.findById(city.getName()).orElseThrow(() -> new IllegalAccessException("city is not found"));
        List<UserLocation> userLocations = userLocationRepository.findByCityName(city.getName());
        final double AREA_KOEF = sqrt(authorizedCity.getArea()/PI)/amountOfLocations*1.5;
        System.out.println(AREA_KOEF);
        List<CoordinatesAndK> coordinatesAndKS= null;
        List<LocationCoordinates> coordinates = new ArrayList<>();
        for(int currentAmount = amountOfLocations; currentAmount>0; currentAmount--) {
            try {
                coordinatesAndKS = computeKoefs(userLocations, authorizedCity, currentAmount);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CoordinatesAndK coordinatesAndK = coordinatesAndKS.stream().max((e, b) -> e.k - b.k > 0 ? 1 : -1).orElse(null);
            if(coordinatesAndK != null) {
                coordinates.add(coordinatesAndK.locationCoordinates);
                int finalCurrentAmount = currentAmount;
                userLocations.removeIf(e -> coordinatesAndK.locationCoordinates.equals(e.getLocationCoordinates()));
                userLocations.removeIf(e ->{
                    double eLat = toRadians(e.getLocationCoordinates().getLatitude());
                    double eLon = toRadians(e.getLocationCoordinates().getLongitude());
                    double mLat = toRadians(coordinatesAndK.locationCoordinates.getLatitude());
                    double mLon = toRadians( coordinatesAndK.locationCoordinates.getLongitude());
                    double a = sqrt
                                (
                                                (mLat-eLat)*(mLat-eLat)
                                        +
                                                (eLon-mLon)*(eLon-mLon)*cos((eLat+mLat)/2)
                                )*6371
                            ;
                         return a               <
                                        AREA_KOEF
                        &&
                        userLocations.size() > finalCurrentAmount - 1;});
            }else{
                break;
            }
        }
//TODO finish the task management
        return coordinates;
    }

    private static class CoordinatesAndK{
        LocationCoordinates locationCoordinates;
        double k;

        public CoordinatesAndK(LocationCoordinates locationCoordinates, double k) {
            this.locationCoordinates = locationCoordinates;
            this.k = k;
        }

        @Override
        public String toString() {
            return "CoordinatesAndK{" +
                    "locationCoordinates=" + locationCoordinates +
                    ", k=" + k +
                    '}';
        }
    }


    @Override
    public List<UserLocation> getAllUsers() {
        return null;
    }
}
