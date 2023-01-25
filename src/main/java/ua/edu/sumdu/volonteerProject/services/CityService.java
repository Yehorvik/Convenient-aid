package ua.edu.sumdu.volonteerProject.services;

import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.model.City;

import java.util.List;

public interface CityService {
    public ua.edu.sumdu.volonteerProject.model.City getCityByName(CityDTO cityDTO);
    List<City> getAllCities();
}
