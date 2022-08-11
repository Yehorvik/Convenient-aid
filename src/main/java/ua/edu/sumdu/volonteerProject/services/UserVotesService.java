package ua.edu.sumdu.volonteerProject.services;

import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.model.ChatLocation;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;

import java.sql.Date;
import java.util.List;

public interface UserVotesService {
    public List<LocationCoordinates> getCoordinates(City city);
    public List<LocationCoordinates> getFittedCoordinatesByLocation(City city, int amountOfLocations, Date fromDate) throws IllegalAccessException;
    public List<ChatLocation> findUsersByCity(City city);
}
