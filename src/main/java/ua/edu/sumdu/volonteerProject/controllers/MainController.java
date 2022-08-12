package ua.edu.sumdu.volonteerProject.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.errors.TelegramSendMessageError;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.UserVote;
import ua.edu.sumdu.volonteerProject.services.CityService;
import ua.edu.sumdu.volonteerProject.services.TelegramBotPushingService;
import ua.edu.sumdu.volonteerProject.services.UserVotesService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
public class MainController {

    private final UserVotesService userVotesService;
    private final TelegramBotPushingService telegramBotPushingService;

    private final CityService cityService;

    @GetMapping("/getVotes")
    public List<LocationCoordinates> getAll(@RequestParam String city, @RequestParam LocalDate localDate){
        City currentCity= cityService.getCityByName(new CityDTO(city));
        List<LocationCoordinates> locationCoordinatesList = userVotesService.getCoordinates(currentCity, Date.valueOf(localDate));
        return locationCoordinatesList;
    }


    @GetMapping("/sendLocation")
    public String sendLocations(@RequestParam String cityName) throws IllegalAccessException, TelegramSendMessageError {
        City city =  cityService.getCityByName(new CityDTO(cityName));
        List<LocationCoordinates> locationCoordinatesList = userVotesService.getFittedCoordinatesByLocation(city, 2, Date.valueOf(LocalDate.now().minusDays(1)));
        telegramBotPushingService.pushMessagesToUsers(city, locationCoordinatesList);
        return "ok";
    }


    @GetMapping("/sendMessage")
    public String sendMessage(@RequestParam String city){
        CityDTO cityDTO = new CityDTO(city);
        try {
            telegramBotPushingService.createPoll(cityService.getCityByName(cityDTO));
        } catch (TelegramSendMessageError e) {
            e.printStackTrace();
        }
        return "Message has been send";
    }
}
