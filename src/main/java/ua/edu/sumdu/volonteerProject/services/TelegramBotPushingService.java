package ua.edu.sumdu.volonteerProject.services;


import ua.edu.sumdu.volonteerProject.errors.TelegramSendMessageError;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;

import java.util.List;


public interface TelegramBotPushingService {
    public void pushMessagesToUsers(City city, List<LocationCoordinates> locationCoordinates) throws TelegramSendMessageError;

    public void createPoll(City city) throws TelegramSendMessageError;
}
