package ua.edu.sumdu.volonteerProject.services;

import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.UserLocation;

import java.util.List;


public interface UserLocationService {
    public List<LocationCoordinates> getCoordinates(City city);
    public List<LocationCoordinates> getFittedCoordinatesByLocation(CityDTO city, int amountOfLocations) throws IllegalAccessException;
    public List<UserLocation> getAllUsers();
}
