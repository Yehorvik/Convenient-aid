package ua.edu.sumdu.volonteerProject.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.DTO.SelectedLocationsDTO;
import ua.edu.sumdu.volonteerProject.errors.TelegramSendMessageError;
import ua.edu.sumdu.volonteerProject.errors.WrongAmountException;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.security.CustomUserDetailsService;
import ua.edu.sumdu.volonteerProject.security.JwtUserDetails;
import ua.edu.sumdu.volonteerProject.services.*;
import ua.edu.sumdu.volonteerProject.utils.DtoConverterUtils;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

@RestController
@AllArgsConstructor
//@PreAuthorize("ADMIN")
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/admin")
@Slf4j
public class MainController {
    private final MapValidationErrorService mapValidationErrorService;
    private final UserVotesService userVotesService;
    private final TelegramBotPushingService telegramBotPushingService;
    private final LogHistoryService logHistoryService;
    private final LastPollAndSecurityCheckerService pollAndSecurityCheckerService;
    private final CityService cityService;

    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/getVotes")
    public ResponseEntity<?> getAll(@RequestParam String city){
        City currentCity= cityService.getCityByName(new CityDTO(city));
        return ResponseEntity.ok( userVotesService.getCoordinates(currentCity));
    }
    @GetMapping("/getVotesCount")
    public ResponseEntity<?> getVoteCount(@RequestParam String city){
        City currentCity= cityService.getCityByName(new CityDTO(city));
        return ResponseEntity.ok( userVotesService.getCountByCity(currentCity));
    }
    @GetMapping("/getLocationsByPeriod")
    public ResponseEntity getLocationsByPeriod(@RequestParam String cityName, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        return null;
    }

    @GetMapping("/getCity")
    public ResponseEntity getCity(@RequestParam String city){
        City city1 =  cityService.getCityByName(new CityDTO(city));
        return ResponseEntity.ok(city1);
    }
    @PatchMapping("/resetCity")
    public ResponseEntity updateTheCity(@RequestBody ObjectNode objectNode){
        String city = objectNode.get("city").asText();
        String username = objectNode.get("username").asText();
        City selectedCity = cityService.getCityByName(new CityDTO( city));
        JwtUserDetails userDetails = (JwtUserDetails) userDetailsService.loadUserByUsername(username);
        userDetails.setCity(selectedCity);
        userDetailsService.updateUser(userDetails);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/sendLocation")
    public ResponseEntity sendLocations(@RequestBody SelectedLocationsDTO selectedLocations, BindingResult bindingResult) throws TelegramSendMessageError {
        ResponseEntity errors = mapValidationErrorService.mapErrors(bindingResult);
        if(errors!=null){
            return errors;
        }

        City city =  cityService.getCityByName(new CityDTO(selectedLocations.getCityName()));
        long diff = pollAndSecurityCheckerService.getLastSendMessageExpirationDifferance(city);
        if(diff<0){
            //TODO UNCOMMENT IN RELEASE
            return mapValidationErrorService.getErrorAsMap("sendLocationTimeout", String.valueOf(-diff));
        }
        telegramBotPushingService.pushMessagesToUsers(city, selectedLocations.getCoordinatesList());
        logHistoryService.LogLocationSending(DtoConverterUtils.convertSelectedLocations(selectedLocations));
        return ResponseEntity.ok("locations was successfully sent");
    }

    @GetMapping("/getBestPoints")
    public ResponseEntity<?> getBestFittingPoints(@RequestParam int amountOfPoints, @RequestParam String cityName) throws IllegalAccessException {
        if(amountOfPoints<=0){
            throw new WrongAmountException("the number of requested points is not positive");
        }
        City city = cityService.getCityByName(new CityDTO(cityName));
            List<LocationCoordinates> locationCoordinatesList = userVotesService.getFittedCoordinatesByLocation(city, amountOfPoints);
            return ResponseEntity.ok( locationCoordinatesList);
    }

    @GetMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestParam String city) throws TelegramSendMessageError {
        CityDTO cityDTO = new CityDTO(city);
        City cityObj = cityService.getCityByName(cityDTO);
        long diff = pollAndSecurityCheckerService.getLastPollingExpirationDifferance(cityObj);
        if(diff<0){
            //TODO UNCOMMENT IN RELEASE
            return mapValidationErrorService.getErrorAsMap("pollingTimeout", String.valueOf(-diff));
        }
        telegramBotPushingService.createPoll(cityObj);
        return ResponseEntity.ok("your poll was successfully send");
    }
}
