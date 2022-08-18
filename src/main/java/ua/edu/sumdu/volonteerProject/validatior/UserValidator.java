package ua.edu.sumdu.volonteerProject.validatior;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ua.edu.sumdu.volonteerProject.DTO.UserDTO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {


    // digit + lowercase char + uppercase char + punctuation + symbol
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO)target;
        if(!userDTO.getPassword().equals(userDTO.getRepeatPassword())){
            errors.rejectValue("password", "Repeat", "Password must match");
        }
        Matcher matcher = pattern.matcher(userDTO.getPassword());
        if(!matcher.matches()){
            errors.rejectValue("password", "Correctness", "Password should have at least 1 digit, 1 lowercase letter, 1 uppercase, symbol of punctuation and 8 characters");
        }
    }
}
