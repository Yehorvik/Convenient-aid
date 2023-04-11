package ua.edu.sumdu.volonteerProject.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.sumdu.volonteerProject.security.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    public Authority findAuthorityByAuthority(String authority);

}
