package ua.edu.sumdu.volonteerProject.controllers;

import org.jboss.resteasy.spi.touri.MappedBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.edu.sumdu.volonteerProject.DTO.UserDTO;
import ua.edu.sumdu.volonteerProject.security.JwtUserDetails;
import ua.edu.sumdu.volonteerProject.utils.DtoConverterUtils;

import javax.persistence.Version;
import javax.validation.Valid;

@Controller
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserDetailsManager userDetailsManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserDTO userDTO){
        if(!userDTO.getPassword().equals(userDTO.getRepeatPassword())){
            return ResponseEntity.unprocessableEntity().body("password dont match");

        }
        JwtUserDetails jwtUserDetails = DtoConverterUtils.convertUserDetails(userDTO);
        userDetailsManager.createUser(jwtUserDetails);
        return ResponseEntity.ok("user successfully created");
    }
}
