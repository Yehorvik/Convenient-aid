package ua.edu.sumdu.volonteerProject.errorHandlers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.edu.sumdu.volonteerProject.errors.*;
import ua.edu.sumdu.volonteerProject.security.Authority;
import ua.edu.sumdu.volonteerProject.services.MapValidationErrorService;

import java.util.HashMap;
import java.util.logging.LogManager;

@RestController
@ControllerAdvice
@AllArgsConstructor
@Slf4j
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private final String BASE_ERROR = "baseError";
    private final String BASIC_MESSAGE = "something went wrong while we sending your message";
    private final String CITY_ERROR = "cityError";
    private final String CITY_ERROR_MESSAGE = "city does not exist";
    private final String AMOUNT_ERROR = "amountError";
    private final String AMOUNT_ERROR_MESSAGE = "amount is not set propertly";
    private final String TELEGRAM_ERROR = "telegramError";
    private final String TELEGRAM_ERROR_MESSAGE = "something went wrong while we sending your message";

    private final String USERNAME_NOT_FOUND = "usernameNotFoundError";

    private final String AUTHORITY_NOT_FOUND = "authorityNotFoundError";
    private final String AUTHORITY_NOT_FOUND_MESSAGE = "cant find a given authority to add it!";
    private final String USERNAME_NOT_FOUND_MESSAGE = "cant find the user with a given email";
    private final MapValidationErrorService mapValidationErrorService;

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public final ResponseEntity<Object> handleUsernameAlreadyExistException(UsernameAlreadyExistException ex, WebRequest request){
        UsernameAlreadyExist exept = new UsernameAlreadyExist(ex.getMessage());
        log.warn(ex.getMessage());
        return new ResponseEntity<>(exept, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<Object> handleUsernameNotFoundException(UsernameAlreadyExistException ex, WebRequest request){
        log.warn(ex.getMessage());
        return (ResponseEntity<Object>) mapValidationErrorService.getErrorAsMap(USERNAME_NOT_FOUND, USERNAME_NOT_FOUND_MESSAGE);
    }

    @ExceptionHandler(AuthorityNotFoundException.class)
    public final ResponseEntity<Object> handleAuthorityNotFoundException(AuthorityNotFoundException ex, WebRequest request){
        log.warn(ex.getMessage());
        return (ResponseEntity<Object>) mapValidationErrorService.getErrorAsMap(AUTHORITY_NOT_FOUND, AUTHORITY_NOT_FOUND_MESSAGE);
    }

    @ExceptionHandler(CityNotFoundException.class)
    public final ResponseEntity<Object> handleCityNotFoundException(CityNotFoundException ex, WebRequest request){
        log.warn(ex.getMessage());
        return (ResponseEntity<Object>) mapValidationErrorService.getErrorAsMap(CITY_ERROR, CITY_ERROR_MESSAGE);
    }

    @ExceptionHandler(TelegramSendMessageError.class)
    public final ResponseEntity handleTelegramSendMessageError(TelegramSendMessageError error, WebRequest request){
        log.error("Telegram send message error", error);
        return mapValidationErrorService.getErrorAsMap(TELEGRAM_ERROR,TELEGRAM_ERROR_MESSAGE);
    }

    @ExceptionHandler(WrongAmountException.class)
    public final ResponseEntity handleWrongAmountException(WrongAmountException err, WebRequest request){
        log.warn("amount error likely in main controller class",err.getMessage());
        return mapValidationErrorService.getErrorAsMap(AMOUNT_ERROR,AMOUNT_ERROR_MESSAGE);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public final ResponseEntity handleIllegalAccessException(IllegalArgumentException ex, WebRequest request){
        log.warn(ex.getMessage());
        return mapValidationErrorService.getErrorAsMap(BASE_ERROR,BASIC_MESSAGE );
    }
}
