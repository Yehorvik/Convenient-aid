package ua.edu.sumdu.volonteerProject.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.errors.TelegramSendMessageError;
import ua.edu.sumdu.volonteerProject.services.CityService;
import ua.edu.sumdu.volonteerProject.services.TelegramBotPushingService;

@RestController
@AllArgsConstructor
public class MainController {


    private final TelegramBotPushingService telegramBotPushingService;

    private final CityService cityService;

    @GetMapping("/getAll")
    public String getAll(){
        return "THAT IS ALL!!";
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
