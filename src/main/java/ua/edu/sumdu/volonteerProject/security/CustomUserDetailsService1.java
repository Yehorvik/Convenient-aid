package ua.edu.sumdu.volonteerProject.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.repos.JwtUserDetailsRepository;

import java.util.Collection;

@Service
@AllArgsConstructor
public class CustomUserDetailsService1 implements UserDetailsService {

    private final JwtUserDetailsRepository jwtUserDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return jwtUserDetailsRepository.getJwtUserDetailsByUsername(username);
    }
}
