package ua.edu.sumdu.volonteerProject.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.repos.JwtUserDetailsRepository;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsManager {
    private final JwtUserDetailsRepository userDetailsRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsRepository.getJwtUserDetailsByUsername(username);
    }

    @Override
    public void createUser(UserDetails user) {
        userDetailsRepository.save((JwtUserDetails) user);
    }

    @Override
    public void updateUser(UserDetails user) {
        userDetailsRepository.save((JwtUserDetails) user);
    }

    @Override
    public void deleteUser(String username) {
        userDetailsRepository.deleteByUsername(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return userDetailsRepository.existsJwtUserDetailsByUsername(username);
    }
}
