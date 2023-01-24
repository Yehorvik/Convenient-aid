package ua.edu.sumdu.volonteerProject.controllers;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.edu.sumdu.volonteerProject.services.CityService;
import ua.edu.sumdu.volonteerProject.services.MapValidationErrorService;

@Controller
@AllArgsConstructor
@Slf4j
@CrossOrigin
@RequestMapping("/volunteers")

public class VolunteerController {

    private final CityService cityService;
    private final MapValidationErrorService errorService;

    @GetMapping("/getCities")
    public ResponseEntity getAllCities(){
        return ResponseEntity.ok(cityService.getAllCities());
    }
}
