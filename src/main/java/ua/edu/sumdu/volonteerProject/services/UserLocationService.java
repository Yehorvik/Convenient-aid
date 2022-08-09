package ua.edu.sumdu.volonteerProject.services;

import ua.edu.sumdu.volonteerProject.DTO.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.ChatLocation;

import java.util.List;


public interface UserLocationService {
    public List<LocationCoordinates> getCoordinates(City city);
    public List<LocationCoordinates> getFittedCoordinatesByLocation(City city, int amountOfLocations) throws IllegalAccessException;
    public List<ChatLocation> findUsersByCity(City city);
}
