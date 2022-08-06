package ua.edu.sumdu.volonteerProject.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/getAll")
    public String getAll(){
        return "THAT IS ALL!!";
    }
}
