package ua.edu.sumdu.volonteerProject.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.errors.AuthorityNotFoundException;
import ua.edu.sumdu.volonteerProject.repos.AuthorityRepository;
import ua.edu.sumdu.volonteerProject.repos.JwtUserDetailsRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsManager {
    private final JwtUserDetailsRepository userDetailsRepository;
    private final AuthorityRepository authorityRepository;
    private final String VOLUNTEER = "VOLUNTEER";
    private final String USER = "USER";

    private final PasswordEncoder bCryptPasswordEncoder;

    public JwtUserDetails loadUserByUUID(UUID uuid){
        return userDetailsRepository.findById(uuid).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public JwtUserDetails loadUserByEmail(String email){
        return userDetailsRepository.findByUsername(email);
    }

    //blocks user if there is a true in the isBlocked parameter
    public void setUserBlocked(String email, boolean isBlocked){
        JwtUserDetails jwtUserDetails = loadUserByEmail(email);
        if(jwtUserDetails==null){
            throw new UsernameNotFoundException("Cant find a user with a given email " + email);
        }
        jwtUserDetails.setBlocked(isBlocked);
        userDetailsRepository.save(jwtUserDetails);
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!userDetailsRepository.existsJwtUserDetailsByUsername(username)) {
            throw new UsernameNotFoundException("User not found");
        }
        return userDetailsRepository.getJwtUserDetailsByUsername(username);
    }

    @Transactional
    public void createUser(UserDetails user) {
        ((JwtUserDetails)user).setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Authority authority = authorityRepository.findAuthorityByAuthority(USER);
        ((JwtUserDetails)user).addAuthority(authority);
        userDetailsRepository.save((JwtUserDetails) user);
    }

    @Override
    public void updateUser(UserDetails user) {
        userDetailsRepository.save((JwtUserDetails) user);
    }

    public void addUserNewRoleByEmail(String email, String authorityName) throws AuthorityNotFoundException {
        JwtUserDetails jwtUserDetails = loadUserByEmail(email);
        if(jwtUserDetails==null){
            throw new UsernameNotFoundException("Cant find a user with a given email " + email);
        }
        Authority authority = authorityRepository.findAuthorityByAuthority(authorityName);
        if(authority==null){
            throw new AuthorityNotFoundException("Cant find an authority");
        }
        jwtUserDetails.addAuthority(authority);
        userDetailsRepository.save(jwtUserDetails);
    }
    public void deleteUserRoleByEmail(String email, String authorityName) throws AuthorityNotFoundException {
        JwtUserDetails jwtUserDetails = loadUserByEmail(email);
        if(jwtUserDetails==null){
            throw new UsernameNotFoundException("Cant find a user with a given email " + email);
        }
        Authority authority = authorityRepository.findAuthorityByAuthority(authorityName);
        if(authority==null){
            throw new AuthorityNotFoundException("Cant find an authority");
        }
        jwtUserDetails.deleteAuthority(authority);
        userDetailsRepository.save(jwtUserDetails);
    }

    public long getCountOfUsers(){
        return userDetailsRepository.count();
    }

    public List<JwtUserDetails> getAllUsersByPage(int page){
        Pageable page1 = PageRequest.of(page, 20);
        return userDetailsRepository.findAll(page1).getContent();
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
