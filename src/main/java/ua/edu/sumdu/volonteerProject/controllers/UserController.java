package ua.edu.sumdu.volonteerProject.controllers;

import lombok.AllArgsConstructor;
import org.jboss.resteasy.spi.touri.MappedBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.edu.sumdu.volonteerProject.DTO.UserDTO;
import ua.edu.sumdu.volonteerProject.errors.UsernameAlreadyExistException;
import ua.edu.sumdu.volonteerProject.payload.JwtLoginSuccessResponse;
import ua.edu.sumdu.volonteerProject.payload.LoginRequest;
import ua.edu.sumdu.volonteerProject.security.JwtTokenProvider;
import ua.edu.sumdu.volonteerProject.security.JwtUserDetails;
import ua.edu.sumdu.volonteerProject.security.SecurityConstraints;
import ua.edu.sumdu.volonteerProject.utils.DtoConverterUtils;
import ua.edu.sumdu.volonteerProject.validatior.UserValidator;

import javax.persistence.Version;
import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserDetailsManager userDetailsManager;
    private final UserValidator userValidator;

    private final JwtTokenProvider tokenProvider;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody  UserDTO userDTO, BindingResult bindingResult){
        if(!userDTO.getPassword().equals(userDTO.getRepeatPassword())){
            return ResponseEntity.unprocessableEntity().body("password dont match");
        }
        userValidator.validate(userDTO, bindingResult);
        if(userDetailsManager.userExists(userDTO.getUsername())){
            throw new UsernameAlreadyExistException("username " + userDTO.getUsername()+ " is already exist");
        }
        JwtUserDetails jwtUserDetails = DtoConverterUtils.convertUserDetails(userDTO);
        userDetailsManager.createUser(jwtUserDetails);
        return ResponseEntity.ok("user successfully created");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult){

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                 loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstraints.TOKEN_PREFIX + tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtLoginSuccessResponse(true, jwt));
    }

}
