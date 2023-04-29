package ua.edu.sumdu.volonteerProject.services;

import ua.edu.sumdu.volonteerProject.DTO.CityName;
import ua.edu.sumdu.volonteerProject.model.City;

import java.util.List;

public interface CityService {
    City getCityByName(CityName cityName);
    List<City> getAllCities();

    void saveCities(City city);
}
