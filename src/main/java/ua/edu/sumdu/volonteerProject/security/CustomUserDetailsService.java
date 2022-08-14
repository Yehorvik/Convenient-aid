package ua.edu.sumdu.volonteerProject.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.repos.JwtUserDetailsRepository;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final JwtUserDetailsRepository userDetailsRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsRepository.getJwtUserDetailsByUsername(username);
    }
}
