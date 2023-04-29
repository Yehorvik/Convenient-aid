package ua.edu.sumdu.volonteerProject.validatior;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ua.edu.sumdu.volonteerProject.DTO.CityDTO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CityValidator implements Validator {
    private static final String NAME_PATTERN = "^[A-Z][a-z]*$";
    private static final int MAX_LONGITUDE = 90;
    private static final int MAX_LATITUDE = 180;
    private static final Pattern pattern = Pattern.compile(NAME_PATTERN);
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(CityDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CityDTO cityDTO = (CityDTO) target;
        if(Math.abs( cityDTO.getLocationCoordinates().getLongitude())>MAX_LONGITUDE){
            errors.rejectValue("locationCoordinates", "Correctness", "longitude is set incorrectly");
        } else if (Math.abs( cityDTO.getLocationCoordinates().getLongitude())>MAX_LATITUDE) {
            errors.rejectValue("locationCoordinates", "Correctness", "longitude is set incorrectly");
        }else if (cityDTO.getArea()<=0
        ){
            errors.rejectValue("area", "Correctness", "area should be positive");
        }
        Matcher matcher = pattern.matcher(cityDTO.getName());
        if(!matcher.matches()){
            errors.rejectValue("name", "NotUppercase", "Name should be started from capitalized letter!");
        }
    }
}
