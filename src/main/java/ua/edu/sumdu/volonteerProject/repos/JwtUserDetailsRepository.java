package ua.edu.sumdu.volonteerProject.repos;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.sumdu.volonteerProject.security.JwtUserDetails;

import javax.persistence.EntityManager;
import java.util.UUID;

public interface JwtUserDetailsRepository extends JpaRepository<JwtUserDetails, UUID> {
    public JwtUserDetails getJwtUserDetailsByUsername(String username);

    public void deleteByUsername(String username);

    public boolean existsJwtUserDetailsByUsername(String username);
}