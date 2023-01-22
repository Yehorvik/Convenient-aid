package ua.edu.sumdu.volonteerProject.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.ChatLocation;
import ua.edu.sumdu.volonteerProject.model.UserVote;
import ua.edu.sumdu.volonteerProject.repos.CitiesRepo;
import ua.edu.sumdu.volonteerProject.repos.UserVotesRepository;

import java.sql.Date;
import java.util.*;

@ExtendWith(
        {MockitoExtension.class}
)
class UserVotesServiceImplTest {

    @Mock
    private CitiesRepo citiesRepo;

    @Mock
    private UserVotesRepository userVotesRepository;

    @InjectMocks
    private UserVotesServiceImpl userLocationService;

    private List<UserVote> userVoteLocationList;

    private List<UserVote> staticUserVoteList;

    private ua.edu.sumdu.volonteerProject.model.City sum;

    @BeforeEach
    private void setup(){
        userVoteLocationList = new ArrayList<>();
        Random random = new Random();
        LocationCoordinates sumy = new LocationCoordinates(50.9216, 34.80029);
        staticUserVoteList = new ArrayList<>();
        sum = new City("sumy", 95);
        for(int i = 0; i < 100000; i++){
            double lat = random.nextGaussian(sumy.getLatitude(), 0.1);
            double lon = random.nextGaussian(sumy.getLongitude(), 0.1);

            //userVoteLocationList.add(new UserVote(UUID.randomUUID(), new Date(2022, 8, 10), new ChatLocation(sum,new LocationCoordinates(lon, lat), 11111111)));
        }
        //staticUserVoteList.add(new ChatLocation(sum, UUID.randomUUID(), new LocationCoordinates(50.915444456344225, 34.754401351662146),random.nextLong()));
        //staticUserVoteList.add(new ChatLocation(sum, UUID.randomUUID(), new LocationCoordinates(50.81930734543224, 34.82120508973681),random.nextLong()));
        //staticUserVoteList.add(new ChatLocation(sum, UUID.randomUUID(), new LocationCoordinates(50.977450747454, 34.74226691157899),random.nextLong()));
        //staticUserVoteList.add(new ChatLocation(sum, UUID.randomUUID(), new LocationCoordinates(50.917924, 34.760380),random.nextLong()));
        //staticUserVoteList.add(new ChatLocation(sum, UUID.randomUUID(), new LocationCoordinates(50.935260, 34.805367),random.nextLong()));

    }

    @Test
    void getFittedCoordinatesByLocation() throws IllegalAccessException {
        BDDMockito.given(citiesRepo.findById("sumy")).willReturn(Optional.of(sum));
        BDDMockito.given(userVotesRepository.getUserVotesByDateOfAnswerGreaterThanAndChatLocation_CityName(new Date(2022, 8, 10), sum)).willReturn(userVoteLocationList);
        //BDDMockito.given(userLocationRepository.findByCityName("sumy")).willReturn(userLocationList);

        //System.out.println(userLocationList);
        long time = System.currentTimeMillis();
// some code
        System.out.println( userLocationService.getFittedCoordinatesByLocation(sum, 3));
        System.out.println((System.currentTimeMillis() - time)/1000./60);


    }
}