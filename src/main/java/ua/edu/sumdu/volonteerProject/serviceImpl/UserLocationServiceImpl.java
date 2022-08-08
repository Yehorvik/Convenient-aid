package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.Logger;
//import org.modelmapper.ModelMapper;
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
public class UserLocationServiceImpl implements UserLocationService {
    private static int CORES = 4;
    private static final double SOME_REGULAR_CONSTANT_KOEF = 10;
    private static final double LAT_TO_KM = 110.573;
    private static final double LON_TO_KM = 111.320;

   // private final ModelMapper modelMapper;
    private final UserLocationRepository userLocationRepository;

    private final CitiesRepo citiesRepo;

    private List<CoordinatesAndK> computeInnerKoefs(UserLocation currentUser, List<UserLocation> allUsers, int from, int to){
        return null;
    }

    private List<CoordinatesAndK> computeKoefsMT(List<UserLocation> userLocations,City city, int amount, int from, int to) {
        List<CoordinatesAndK> coordinatesAndKS = new ArrayList<>();
        for (int i= from; i<to; i++) {
            CoordinatesAndK coordinatesAndK = new CoordinatesAndK(userLocations.get(i).getLocationCoordinates(), 0);
            final double SQUARE_KOEF = SOME_REGULAR_CONSTANT_KOEF * (double) amount / city.getArea();

            for (UserLocation t : userLocations) {
                LocationCoordinates innerLC = t.getLocationCoordinates();
                LocationCoordinates outerLC = coordinatesAndK.locationCoordinates;
                coordinatesAndK.k += exp(SQUARE_KOEF * -sqrt(
                        pow((innerLC.getLatitude()) - outerLC.getLatitude(), 2)
                                +
                                pow((innerLC.getLongitude()) - outerLC.getLongitude(), 2)
                ));
            }
            coordinatesAndKS.add(coordinatesAndK);
        }
        return coordinatesAndKS;
    }

    private List<CoordinatesAndK> computeKoefs(List<UserLocation> userLocations,City city, int amount) throws ExecutionException, InterruptedException {
        List<CompletableFuture> completableFutures = new ArrayList<>();
        /*for(int i = 1 ; i<= CORES && userLocations.size()>CORES; i++){
            int finalI = i;
        }
        */completableFutures.add(  CompletableFuture.supplyAsync(()-> this.computeKoefsMT(userLocations,city,amount, 0,userLocations.size()/4)));
        completableFutures.add(CompletableFuture.supplyAsync(()-> this.computeKoefsMT(userLocations,city,amount,userLocations.size()/4,userLocations.size()/2)));
        completableFutures.add(CompletableFuture.supplyAsync(()-> this.computeKoefsMT(userLocations,city,amount,userLocations.size()/2,(userLocations.size()*3)/4)));
        completableFutures.add(CompletableFuture.supplyAsync(()-> this.computeKoefsMT(userLocations,city,amount,((userLocations.size()*3)/4),userLocations.size())));
        List<CoordinatesAndK> list = new ArrayList<>();
        for (CompletableFuture t:
             completableFutures) {
            list.addAll(
            (List<CoordinatesAndK>)t.get());
        }

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
        final double LOCATION_LAT_KOEF = authorizedCity.getArea()/((double) amountOfLocations*LAT_TO_KM * SOME_REGULAR_CONSTANT_KOEF/2);
        final double LOCATION_LON_KOEF = authorizedCity.getArea()/((double) amountOfLocations*LON_TO_KM * SOME_REGULAR_CONSTANT_KOEF/2);
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
                userLocations.removeIf(e -> (
                        abs(e.getLocationCoordinates().getLatitude() - coordinatesAndK.locationCoordinates.getLatitude())
                                <
                                LOCATION_LAT_KOEF
                                ||
                                abs(e.getLocationCoordinates().getLongitude() - coordinatesAndK.locationCoordinates.getLongitude())
                                        <
                                        LOCATION_LON_KOEF / cos(coordinatesAndK.locationCoordinates.getLatitude() * PI / 180))
                        &&
                        userLocations.size() > finalCurrentAmount - 1);
                //System.out.println("LOCATION LAT KOEF: " + LOCATION_LAT_KOEF + " LOCATION LON KOEF: " + LOCATION_LON_KOEF);
                //System.out.println(coordinatesAndKS);
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
