package ua.edu.sumdu.volonteerProject.services;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface MapValidationErrorService {
    public ResponseEntity<?> mapErrors(BindingResult bindingResult);

    ResponseEntity<?> getErrorAsMap(String ERROR_TYPE, String ERROR_MSG);
}
