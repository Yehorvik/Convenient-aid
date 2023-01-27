package ua.edu.sumdu.volonteerProject.utils;

import ua.edu.sumdu.volonteerProject.DTO.SelectedLocationsDTO;
import ua.edu.sumdu.volonteerProject.DTO.UserDTO;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.SendLocationsDetails;
import ua.edu.sumdu.volonteerProject.security.Authority;
import ua.edu.sumdu.volonteerProject.security.JwtUserDetails;

import java.util.List;

public class DtoConverterUtils {
    public static JwtUserDetails convertUserDetails(UserDTO userDTO){
        Authority a=  new Authority();
        a.setAuthority("USER");
        return new JwtUserDetails(
                 List.of(a),
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
