package ua.edu.sumdu.volonteerProject.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.edu.sumdu.volonteerProject.DTO.CityDTO;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.services.CityService;
import ua.edu.sumdu.volonteerProject.services.MapValidationErrorService;
import ua.edu.sumdu.volonteerProject.utils.DtoConverterUtils;
import ua.edu.sumdu.volonteerProject.validatior.CityValidator;

@RestController
@AllArgsConstructor
//@PreAuthorize("ADMIN")
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/admin")
public class CityController {

    private final MapValidationErrorService validationErrorService;
    private final CityValidator cityValidator;

    private final CityService cityService;

    @PostMapping("/addCity")
    public ResponseEntity<?> addCity(@RequestBody CityDTO cityDTO, BindingResult bindingResult){
        cityValidator.validate(cityDTO, bindingResult);
        ResponseEntity errors = validationErrorService.mapErrors(bindingResult);
        if(errors!=null){
            return errors;
        }
        City city =  DtoConverterUtils.convertCity(cityDTO);
        cityService.saveCities(city);
        return ResponseEntity.ok().build();
    }
}
