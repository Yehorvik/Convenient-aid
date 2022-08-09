package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.DTO.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.ChatLocation;
import ua.edu.sumdu.volonteerProject.repos.CitiesRepo;
import ua.edu.sumdu.volonteerProject.repos.UserLocationRepository;
import ua.edu.sumdu.volonteerProject.services.UserLocationService;
import ua.edu.sumdu.volonteerProject.utils.CoordinateUtils;

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


    private double computeInnerKoefs(ChatLocation currentUser, List<ChatLocation> allUsers, double cityArea, int amount, int from, int to){
        double retValue = 0;

        CoordinatesAndK coordinatesAndK = new CoordinatesAndK(currentUser.getLocationCoordinates(), 0);
        final double SQUARE_KOEF = CONSTANT_FOR_EXP * (double) amount / cityArea;
        for (int i = from; i < to; i++) {
            LocationCoordinates innerLC = allUsers.get(i).getLocationCoordinates();
            LocationCoordinates outerLC = coordinatesAndK.locationCoordinates;
            //latitude

//            double x = (toRadians(innerLC.getLatitude())) - toRadians(outerLC.getLatitude());
//            //longitude
//            double y = (toRadians(innerLC.getLongitude() - outerLC.getLongitude()))*cos((innerLC.getLatitude()+outerLC.getLatitude())*PI/2/180);
//            sqrt(
//                    x*x + y*y
//            )
            double distanceInKm = CoordinateUtils.calculateDistance(innerLC, outerLC);
            retValue += exp(SQUARE_KOEF * (-distanceInKm));
        }
        return retValue;
    }

    private List<CoordinatesAndK> computeKoefsMT(List<ChatLocation> chatLocations, ua.edu.sumdu.volonteerProject.model.City city, int amount, int from, int to) {

        List<CoordinatesAndK> coordinatesAndKS = new ArrayList<>();
        for (int i= from; i<to; i++) {
            List<CompletableFuture<Double>> completableFutures = new ArrayList<>();
            CoordinatesAndK coordinatesAndK = new CoordinatesAndK(chatLocations.get(i).getLocationCoordinates(), 0);
            int finalI = i;
            completableFutures.add(  CompletableFuture.supplyAsync(()-> this.computeInnerKoefs(chatLocations.get(finalI), chatLocations,city.getArea(),amount, 0, chatLocations.size()/4)).exceptionally((e) -> {log.error("exception in  computeKoefsMT",e); return null;}));
            completableFutures.add(CompletableFuture.supplyAsync(()-> this.computeInnerKoefs(chatLocations.get(finalI), chatLocations,city.getArea(),amount, chatLocations.size()/4, chatLocations.size()/2)).exceptionally((e) -> {log.error("exception in  computeKoefsMT",e); return null;}));
            completableFutures.add(CompletableFuture.supplyAsync(()-> this.computeInnerKoefs(chatLocations.get(finalI), chatLocations,city.getArea(),amount, chatLocations.size()/2, chatLocations.size()*3/4)).exceptionally((e) -> {log.error("exception in  computeKoefsMT",e); return null;}));
            completableFutures.add(CompletableFuture.
                    supplyAsync(()-> this.computeInnerKoefs(chatLocations.get(finalI), chatLocations,city.getArea(),amount, chatLocations.size()*3/4, chatLocations.size())).exceptionally((e) -> {log.error("exception in  computeKoefsMT",e); return null;}));
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

    private List<CoordinatesAndK> computeKoefs(List<ChatLocation> chatLocations, ua.edu.sumdu.volonteerProject.model.City city, int amount) throws ExecutionException, InterruptedException {
        List<CompletableFuture> completableFutures = new ArrayList<>();
        /*for(int i = 1 ; i<= CORES && userLocations.size()>CORES; i++){
            int finalI = i;
        }
        */
            completableFutures.add(CompletableFuture.supplyAsync(() -> this.computeKoefsMT(chatLocations, city, amount, 0, chatLocations.size() / 4)));
            completableFutures.add(CompletableFuture.supplyAsync(() -> this.computeKoefsMT(chatLocations, city, amount, chatLocations.size() / 4, chatLocations.size() / 2)));
            completableFutures.add(CompletableFuture.supplyAsync(() -> this.computeKoefsMT(chatLocations, city, amount, chatLocations.size() / 2, (chatLocations.size() * 3) / 4)));
            completableFutures.add(CompletableFuture.supplyAsync(() -> this.computeKoefsMT(chatLocations, city, amount, ((chatLocations.size() * 3) / 4), chatLocations.size())));
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
        citiesRepo.findById(city.getName()).orElseThrow(() -> {return new NullPointerException("city does not exist!");});
        return userLocationRepository.findByCityName(city.getName()).stream().map(e->{return e.getLocationCoordinates();}).collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Transactional
    public List<LocationCoordinates> getFittedCoordinatesByLocation(City city, int amountOfLocations) throws IllegalAccessException {
        if(amountOfLocations <=0){
            throw new IllegalAccessException("amount of locations cant be negative");
        }
        ua.edu.sumdu.volonteerProject.model.City authorizedCity = citiesRepo.findById(city.getName()).orElseThrow(() -> new IllegalAccessException("city is not found"));
        List<ChatLocation> chatLocations = userLocationRepository.findByCityName(city.getName());
        final double AREA_KOEF = sqrt(authorizedCity.getArea()/PI)/amountOfLocations*1.5;
        System.out.println(AREA_KOEF);
        List<CoordinatesAndK> coordinatesAndKS= null;
        List<LocationCoordinates> coordinates = new ArrayList<>();
        for(int currentAmount = amountOfLocations; currentAmount>0; currentAmount--) {
            try {
                coordinatesAndKS = computeKoefs(chatLocations, authorizedCity, currentAmount);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CoordinatesAndK coordinatesAndK = coordinatesAndKS.stream().max((e, b) -> e.k - b.k > 0 ? 1 : -1).orElse(null);
            if(coordinatesAndK != null) {
                coordinates.add(coordinatesAndK.locationCoordinates);
                int finalCurrentAmount = currentAmount;
                chatLocations.removeIf(e -> coordinatesAndK.locationCoordinates.equals(e.getLocationCoordinates()));
                chatLocations.removeIf(e ->{
                         double distance = CoordinateUtils.calculateDistance(e.getLocationCoordinates(), coordinatesAndK.locationCoordinates);
                         return distance < AREA_KOEF && chatLocations.size() > finalCurrentAmount - 1;});
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
    @Transactional
    public List<ChatLocation> findUsersByCity(City city) {
        citiesRepo.findById(city.getName()).orElseThrow(() -> {return new NullPointerException("city does not exist!");});
        return userLocationRepository.findByCityName(city.getName());
    }
}
