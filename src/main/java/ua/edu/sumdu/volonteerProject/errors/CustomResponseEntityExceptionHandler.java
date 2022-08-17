package ua.edu.sumdu.volonteerProject.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestController
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public final ResponseEntity<Object> handleUsernameAlreadyExistException(UsernameAlreadyExistException ex, WebRequest request){
        UsernameAlreadyExist exept = new UsernameAlreadyExist(ex.getMessage());
        return new ResponseEntity<>(exept, HttpStatus.BAD_REQUEST);
    }
}
