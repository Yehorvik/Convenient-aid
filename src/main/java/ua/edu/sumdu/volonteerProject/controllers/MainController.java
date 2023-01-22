package ua.edu.sumdu.volonteerProject.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.DTO.SelectedLocationsDTO;
import ua.edu.sumdu.volonteerProject.errors.TelegramSendMessageError;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.services.CityService;
import ua.edu.sumdu.volonteerProject.services.LogHistoryService;
import ua.edu.sumdu.volonteerProject.services.TelegramBotPushingService;
import ua.edu.sumdu.volonteerProject.services.UserVotesService;
import ua.edu.sumdu.volonteerProject.utils.DtoConverterUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@PreAuthorize(value = "ADMIN")
public class MainController {

    private final UserVotesService userVotesService;
    private final TelegramBotPushingService telegramBotPushingService;
    private final LogHistoryService logHistoryService;

    private final CityService cityService;

    @GetMapping("/getVotes")
    public List<LocationCoordinates> getAll(@RequestParam String city, @RequestParam LocalDate localDate){
        City currentCity= cityService.getCityByName(new CityDTO(city));
        return userVotesService.getCoordinates(currentCity, Date.valueOf(localDate));
    }

    @GetMapping("/getLocationsByPeriod")
    public ResponseEntity getLocationsByPeriod(@RequestParam String cityName, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        return null;
    }

    @PostMapping("/sendLocation")
    public ResponseEntity sendLocations(@RequestBody SelectedLocationsDTO selectedLocations) throws IllegalAccessException, TelegramSendMessageError {
        City city =  cityService.getCityByName(new CityDTO(selectedLocations.getCity()));
        telegramBotPushingService.pushMessagesToUsers(city, selectedLocations.getCoordinatesList());
        logHistoryService.LogLocationSending(DtoConverterUtils.convertSelectedLocations(selectedLocations));
        return ResponseEntity.ok("locations was successfully sent");
    }

    @GetMapping("/getBestPoints")
    public ResponseEntity<?> getBestFittingPoints(@RequestParam int amountOfPoints, @RequestParam String cityName){
        City city = cityService.getCityByName(new CityDTO(cityName));
        try {
            List<LocationCoordinates> locationCoordinatesList = userVotesService.getFittedCoordinatesByLocation(city, amountOfPoints);
            return ResponseEntity.ok( locationCoordinatesList);
        } catch (IllegalAccessException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestParam String city){
        CityDTO cityDTO = new CityDTO(city);
        try {
            telegramBotPushingService.createPoll(cityService.getCityByName(cityDTO));
        } catch (TelegramSendMessageError e) {
            e.printStackTrace();
            return new ResponseEntity<String>("something went wrong while we sending your message", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("your poll was successfully send");
    }
}
