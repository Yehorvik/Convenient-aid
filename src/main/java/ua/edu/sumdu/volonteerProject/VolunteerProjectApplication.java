package ua.edu.sumdu.volonteerProject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@ComponentScan(value = {"ua.edu.sumdu.volonteerProject.config","ua.edu.sumdu.volonteerProject.*"})
@Slf4j
public class VolunteerProjectApplication {
	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
	public static void main(String[] args) {
		log.info("APP For volunteers has been started");
		SpringApplication.run(VolunteerProjectApplication.class, args);
		log.info("APP For volunteers is closed");
	}

}
