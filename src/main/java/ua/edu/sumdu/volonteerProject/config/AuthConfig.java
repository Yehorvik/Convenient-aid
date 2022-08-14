package ua.edu.sumdu.volonteerProject.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.edu.sumdu.volonteerProject.repos.JwtUserDetailsRepository;
import ua.edu.sumdu.volonteerProject.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class AuthConfig extends WebSecurityConfigurerAdapter {


    public UserDetailsService userDetailsService(@Autowired JwtUserDetailsRepository jwtUserDetailsRepository){
        return new CustomUserDetailsService(jwtUserDetailsRepository);
    }

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
