package ua.edu.sumdu.volonteerProject.services;

import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;

import java.util.List;

public interface UserVotesService {
    public List<LocationCoordinates> getCoordinates(City city);
    Long getCountByCity(City city);
    public List<LocationCoordinates> getFittedCoordinatesByLocation(City city, int amountOfLocations) throws IllegalAccessException;
    //public List<ChatLocation> findUsersByCity(City city);
}
