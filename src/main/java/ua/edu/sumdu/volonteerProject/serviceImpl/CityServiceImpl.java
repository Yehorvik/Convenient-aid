package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.DTO.CityName;
import ua.edu.sumdu.volonteerProject.errors.CityNotFoundException;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.repos.CitiesRepo;
import ua.edu.sumdu.volonteerProject.services.CityService;

import java.util.List;


@AllArgsConstructor
@Service
public class CityServiceImpl implements CityService {
    private final CitiesRepo citiesRepo;

    @Override
    public ua.edu.sumdu.volonteerProject.model.City getCityByName(CityName cityName) {
        return citiesRepo.findById(cityName.getName()).orElseThrow(() -> new CityNotFoundException("cant find the city"));
    }

    @Override
    public List<City> getAllCities() {
        return citiesRepo.findAll();
    }

    @Override
    public void saveCities(City city) {
        citiesRepo.save(city);
    }


}
