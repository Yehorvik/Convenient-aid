package ua.edu.sumdu.volonteerProject.services;

import ua.edu.sumdu.volonteerProject.model.ChatLocation;
import ua.edu.sumdu.volonteerProject.model.City;

import java.util.List;


public interface ChatLocationService {
    public List<ChatLocation> getChatsByCity(City city);
}
