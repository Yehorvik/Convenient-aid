package ua.edu.sumdu.volonteerProject.repos;

import lombok.AllArgsConstructor;
import org.hibernate.annotations.NamedNativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.edu.sumdu.volonteerProject.security.JwtUserDetails;

import javax.persistence.EntityManager;
import java.util.UUID;

public interface JwtUserDetailsRepository extends JpaRepository<JwtUserDetails, UUID> {
    public JwtUserDetails getJwtUserDetailsByUsername(String username);

    public void deleteByUsername(String username);

    public JwtUserDetails findByUsername(String username);
    public boolean existsJwtUserDetailsByUsername(String username);
}