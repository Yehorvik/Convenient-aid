package ua.edu.sumdu.volonteerProject.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.DTO.SelectedLocationsDTO;
import ua.edu.sumdu.volonteerProject.DTO.UserDTO;
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
    public JwtUserDetails convertUserDetails(UserDTO userDTO){
        Authority a=  new Authority();
        City city = citiesRepo.findById(userDTO.getCityName()).orElse(null);
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
        return new SendLocationsDetails(selectedLocations.getAdminUsername(), null, selectedLocations.getAmountOfPoints(), selectedLocations.getCityName(), null);
    }
}
