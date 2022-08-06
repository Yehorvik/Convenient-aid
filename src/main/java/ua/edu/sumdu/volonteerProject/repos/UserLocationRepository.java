package ua.edu.sumdu.volonteerProject.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.sumdu.volonteerProject.model.UserLocation;

import java.util.List;
import java.util.UUID;

public interface UserLocationRepository extends JpaRepository<UserLocation, UUID> {
    public  List<UserLocation> findByCityName(String name);
}
