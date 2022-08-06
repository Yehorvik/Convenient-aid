package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.UserLocation;
import ua.edu.sumdu.volonteerProject.repos.CitiesRepo;
import ua.edu.sumdu.volonteerProject.repos.UserLocationRepository;
import ua.edu.sumdu.volonteerProject.services.UserLocationService;
import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserLocationServiceImpl implements UserLocationService {

    private final ModelMapper modelMapper;
    private final UserLocationRepository userLocationRepository;

    private final CitiesRepo citiesRepo;

    private List<CoordinatesAndK> computeKoefs(List<UserLocation> userLocations){
        List<CoordinatesAndK> coordinatesAndKS = new ArrayList<>();
        for(UserLocation u : userLocations){
            CoordinatesAndK coordinatesAndK =  new CoordinatesAndK(u.getLocationCoordinates(),1);
            for(UserLocation t : userLocations){
                LocationCoordinates innerLC = t.getLocationCoordinates();
                LocationCoordinates outerLC = coordinatesAndK.locationCoordinates;
                coordinatesAndK.k+=exp(sqrt(
                        pow((innerLC.getLatitude())-outerLC.getLatitude(),2)
                        +
                        pow((innerLC.getLongitude())-outerLC.getLongitude(),2)
                ));
            }
            coordinatesAndKS.add(coordinatesAndK);
        }
        return coordinatesAndKS;
    }

    @Override
    public List<LocationCoordinates> getCoordinates(City city) {
        if(city == null){
            throw new NullPointerException("city cant be null");
        }
        return userLocationRepository.findByCityName(city.getName()).stream().map(e->{return e.getLocationCoordinates();}).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<LocationCoordinates> getFittedCoordinatesByLocation(CityDTO city, long amountOfLocations) {

        City authorizedCity = citiesRepo.getReferenceById(city.getName());
        List<UserLocation> userLocations = userLocationRepository.findByCityName(city.getName());
        List<CoordinatesAndK> coordinatesAndKS = computeKoefs(userLocations);
//TODO finish the task management
        return null;
    }

    private static class CoordinatesAndK{
        LocationCoordinates locationCoordinates;
        double k;

        public CoordinatesAndK(LocationCoordinates locationCoordinates, double k) {
            this.locationCoordinates = locationCoordinates;
            this.k = k;
        }
    }

    @Override
    public List<UserLocation> getAllUsers() {
        return null;
    }
}
