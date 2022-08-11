package ua.edu.sumdu.volonteerProject.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.sumdu.volonteerProject.model.ChatLocation;
import ua.edu.sumdu.volonteerProject.model.City;

import java.util.List;
import java.util.UUID;

public interface ChatLocationRepository extends JpaRepository<ChatLocation, UUID> {
    public  List<ChatLocation> findByCityName(City name);
}
