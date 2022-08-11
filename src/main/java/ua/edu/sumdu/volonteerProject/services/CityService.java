package ua.edu.sumdu.volonteerProject.services;

import ua.edu.sumdu.volonteerProject.DTO.CityDTO;

public interface CityService {
    public ua.edu.sumdu.volonteerProject.model.City getCityByName(CityDTO cityDTO);
}
