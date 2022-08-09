package ua.edu.sumdu.volonteerProject.serviceImpl;

import org.aspectj.lang.annotation.Before;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.service.spi.InjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.UserLocation;
import ua.edu.sumdu.volonteerProject.repos.CitiesRepo;
import ua.edu.sumdu.volonteerProject.repos.UserLocationRepository;
import ua.edu.sumdu.volonteerProject.services.UserLocationService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(
        {MockitoExtension.class}
)
class UserLocationServiceImplTest {

    @Mock
    private CitiesRepo citiesRepo;

    @Mock
    private UserLocationRepository userLocationRepository;

    @InjectMocks
    private UserLocationServiceImpl userLocationService;

    private List<UserLocation> userLocationList;

    private List<UserLocation> staticUserLocationList;

    private City sum;

    @BeforeEach
    private void setup(){
        userLocationList = new ArrayList<>();
        Random random = new Random();
        LocationCoordinates sumy = new LocationCoordinates(50.9216, 34.80029);
        staticUserLocationList = new ArrayList<>();
        sum = new City("sumy", 95);
        for(int i = 0; i < 100000; i++){
            double lat = random.nextGaussian(sumy.getLatitude(), 0.1);
            double lon = random.nextGaussian(sumy.getLongitude(), 0.1);
            userLocationList.add( new UserLocation(sum, UUID.randomUUID(),new LocationCoordinates(lon, lat) ,random.nextLong()));
        }
        staticUserLocationList.add(new UserLocation(sum, UUID.randomUUID(), new LocationCoordinates(50.915444456344225, 34.754401351662146),random.nextLong()));
        staticUserLocationList.add(new UserLocation(sum, UUID.randomUUID(), new LocationCoordinates(50.81930734543224, 34.82120508973681),random.nextLong()));
        staticUserLocationList.add(new UserLocation(sum, UUID.randomUUID(), new LocationCoordinates(50.977450747454, 34.74226691157899),random.nextLong()));
        staticUserLocationList.add(new UserLocation(sum, UUID.randomUUID(), new LocationCoordinates(50.917924, 34.760380),random.nextLong()));
        staticUserLocationList.add(new UserLocation(sum, UUID.randomUUID(), new LocationCoordinates(50.935260, 34.805367),random.nextLong()));

    }

    @Test
    void getFittedCoordinatesByLocation() throws IllegalAccessException {
        BDDMockito.given(citiesRepo.findById("sumy")).willReturn(Optional.of(sum));
        BDDMockito.given(userLocationRepository.findByCityName("sumy")).willReturn(staticUserLocationList);
        //BDDMockito.given(userLocationRepository.findByCityName("sumy")).willReturn(userLocationList);

        //System.out.println(userLocationList);
        long time = System.currentTimeMillis();
// some code
        System.out.println( userLocationService.getFittedCoordinatesByLocation(new CityDTO("sumy"), 3));
        System.out.println((System.currentTimeMillis() - time)/1000./60);


    }
}