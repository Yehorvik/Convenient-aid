package ua.edu.sumdu.volonteerProject.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.sumdu.volonteerProject.security.JwtUserDetails;

import java.util.UUID;

public interface JwtUserDetailsRepository extends JpaRepository<JwtUserDetails, UUID> {
    public JwtUserDetails getJwtUserDetailsByUsername(String username);
}
