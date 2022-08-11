package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.repos.CitiesRepo;
import ua.edu.sumdu.volonteerProject.services.CityService;


@AllArgsConstructor
@Service
public class CityServiceImpl implements CityService {
    private final CitiesRepo citiesRepo;

    @Override
    public ua.edu.sumdu.volonteerProject.model.City getCityByName(CityDTO cityDTO) {
        return citiesRepo.findById(cityDTO.getName()).orElseThrow(() -> new NullPointerException("cant find the city"));
    }
}
