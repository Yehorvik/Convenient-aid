package ua.edu.sumdu.volonteerProject.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.repos.JwtUserDetailsRepository;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsManager {
    private final JwtUserDetailsRepository userDetailsRepository;


    private final PasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(  userDetailsRepository.existsJwtUserDetailsByUsername(username)) {
            throw new UsernameNotFoundException("User not found");
        }
        return userDetailsRepository.getJwtUserDetailsByUsername(username);
    }

    @Override
    public void createUser(UserDetails user) {
        ((JwtUserDetails)user).setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
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
