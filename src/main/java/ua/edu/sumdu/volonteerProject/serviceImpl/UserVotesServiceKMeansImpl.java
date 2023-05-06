package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.clustering.ClusterService;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.UserVote;
import ua.edu.sumdu.volonteerProject.repos.CitiesRepo;
import ua.edu.sumdu.volonteerProject.repos.UserVotesRepository;
import ua.edu.sumdu.volonteerProject.services.UserVotesService;
import ua.edu.sumdu.volonteerProject.utils.CoordinateUtils;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service("kmeansImplementationUserVotesService")
public class UserVotesServiceKMeansImpl implements UserVotesService {

    private final CitiesRepo citiesRepo;
    private final UserVotesRepository userVotesRepository;


    private double computeDistance(UserVote currentUser, List<UserVote> allUsers){
        double retValue = 0;
        for (int i = 0; i < allUsers.size(); i++) {
            LocationCoordinates innerLC = allUsers.get(i).getChatLocation().getLocationCoordinates();
            LocationCoordinates outerLC = currentUser.getChatLocation().getLocationCoordinates();
            double distanceInKm = CoordinateUtils.calculateDistance(innerLC, outerLC);
        }
        return retValue;
    }
    @Override
    public List<LocationCoordinates> getCoordinates(City city) {
        if(city == null){
            throw new NullPointerException("city cant be null!");
        }
        //citiesRepo.findById(city.getName()).orElseThrow(() -> {return new NullPointerException("city does not exist!");});
        return userVotesRepository.getUserVotesByActiveAndChatLocation_CityName(true ,city).stream().map(e->{return e.getChatLocation().getLocationCoordinates();}).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Long getCountByCity(City city) {
        if(city == null){
            throw new NullPointerException("city cant be null!");
        }
        //citiesRepo.findById(city.getName()).orElseThrow(() -> {return new NullPointerException("city does not exist!");});
        return userVotesRepository.countUserVotesByActiveAndChatLocation_CityName(true, city);
    }

    @Override
    public List<LocationCoordinates> getFittedCoordinatesByLocation(City city, int amountOfLocations) throws IllegalAccessException {
        if(amountOfLocations <=0){
            throw new IllegalAccessException("amount of locations cant be negative");
        }
        ua.edu.sumdu.volonteerProject.model.City authorizedCity = citiesRepo.findById(city.getName()).orElseThrow(() -> new IllegalAccessException("city is not found"));
        List<UserVote> chatLocations = userVotesRepository.getUserVotesByActiveAndChatLocation_CityName(true, authorizedCity);

        System.out.println("I AM HERE!!!");
        //KmeansUtil kmeansUtil = new KmeansUtil.Builder(amountOfLocations,chatLocations.stream().map(e->e.getChatLocation().getLocationCoordinates()).toList()).iterations(3).useEpsilon(true).build();
        return  ClusterService.cluster(chatLocations.stream().map(e->e.getChatLocation().getLocationCoordinates()).collect(Collectors.toList()), amountOfLocations).stream().map(e->(LocationCoordinates)e).collect(Collectors.toList());
    }
}
