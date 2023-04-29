package ua.edu.sumdu.volonteerProject.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.edu.sumdu.volonteerProject.DTO.UserDTO;
import ua.edu.sumdu.volonteerProject.errors.UsernameAlreadyExistException;
import ua.edu.sumdu.volonteerProject.payload.JwtLoginSuccessResponse;
import ua.edu.sumdu.volonteerProject.payload.LoginRequest;
import ua.edu.sumdu.volonteerProject.security.CustomUserDetailsService;
import ua.edu.sumdu.volonteerProject.security.JwtTokenProvider;
import ua.edu.sumdu.volonteerProject.security.JwtUserDetails;
import ua.edu.sumdu.volonteerProject.security.SecurityConstraints;
import ua.edu.sumdu.volonteerProject.services.MapValidationErrorService;
import ua.edu.sumdu.volonteerProject.utils.DtoConverterUtils;
import ua.edu.sumdu.volonteerProject.validatior.UserValidator;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("/api/user")
@Slf4j
@CrossOrigin( origins = "http://localhost:3000")
public class UserController {

    private final CustomUserDetailsService userDetailsManager;
    private final UserValidator userValidator;

    private final JwtTokenProvider tokenProvider;

    private final DtoConverterUtils dtoConverterUtils;
    private final AuthenticationManager authenticationManager;

    private final MapValidationErrorService validationErrorService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody  UserDTO userDTO, BindingResult bindingResult){
        userValidator.validate(userDTO, bindingResult);
        ResponseEntity errors = validationErrorService.mapErrors(bindingResult);
        if(errors!=null){
            return errors;
        }
        if(userDetailsManager.userExists(userDTO.getUsername())){
            throw new UsernameAlreadyExistException("username " + userDTO.getUsername()+ " is already exist");
        }
        JwtUserDetails jwtUserDetails = dtoConverterUtils.convertUserDetails(userDTO);
        userDetailsManager.createUser(jwtUserDetails);
        return ResponseEntity.ok("user successfully created");

    }



    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult){

        ResponseEntity<?> errors = validationErrorService.mapErrors(bindingResult);
        if(errors!=null){
            return errors;
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstraints.TOKEN_PREFIX + tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtLoginSuccessResponse(true, jwt));
    }

}
