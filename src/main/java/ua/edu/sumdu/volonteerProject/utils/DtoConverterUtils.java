package ua.edu.sumdu.volonteerProject.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.DTO.SelectedLocationsDTO;
import ua.edu.sumdu.volonteerProject.DTO.UserDTO;
import ua.edu.sumdu.volonteerProject.DTO.UserInfoDTO;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.SendLocationsDetails;
import ua.edu.sumdu.volonteerProject.repos.CitiesRepo;
import ua.edu.sumdu.volonteerProject.security.Authority;
import ua.edu.sumdu.volonteerProject.security.JwtUserDetails;

import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class DtoConverterUtils {
    private final CitiesRepo citiesRepo;

    public static City convertCity(CityDTO cityDTO) {
        return new City(cityDTO.getName(),cityDTO.getLocationCoordinates(), cityDTO.getArea());
    }

    public static UserInfoDTO convertUserInfo(JwtUserDetails jwtUserDetails){
        String cityName =  jwtUserDetails.getCity()!=null?jwtUserDetails.getCity().getName():null;
        return new UserInfoDTO(jwtUserDetails.getUsername(),jwtUserDetails.getFirstName(), jwtUserDetails.getSecondName(), jwtUserDetails.getId(), jwtUserDetails.getAuthorityList().stream().toList(), jwtUserDetails.isBlocked(), cityName);
    }
    public JwtUserDetails convertUserDetails(UserDTO userDTO){
        City city = null;
        if(userDTO.getCityName()!=null) {
             city = citiesRepo.findById(userDTO.getCityName()).get();
        }
        return new JwtUserDetails(
                 new HashSet<>(),
                 city,
                userDTO.getPassword()
                ,userDTO.getFirstName()
                ,userDTO.getSecondName()
                ,null
                ,null
                ,null
                ,userDTO.getUsername()
                ,false

        );
    }

    public static SendLocationsDetails convertSelectedLocations(SelectedLocationsDTO selectedLocations) {
        return new SendLocationsDetails(selectedLocations.getAdminUsername(), null, selectedLocations.getAmountOfPoints(), selectedLocations.getCityName(), selectedLocations.getTimeOfDelivering() ,null);
    }
}
