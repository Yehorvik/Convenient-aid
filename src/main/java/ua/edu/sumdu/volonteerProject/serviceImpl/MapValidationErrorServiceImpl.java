package ua.edu.sumdu.volonteerProject.serviceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ua.edu.sumdu.volonteerProject.services.MapValidationErrorService;

import java.util.HashMap;
import java.util.Map;

@Service
public class MapValidationErrorServiceImpl implements MapValidationErrorService {
    @Override
    public ResponseEntity<?> mapErrors(BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for(FieldError f : bindingResult.getFieldErrors()){
                errors.put(f.getField(),f.getDefaultMessage());
            }
            return new ResponseEntity<Map<String,String>>(errors, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
