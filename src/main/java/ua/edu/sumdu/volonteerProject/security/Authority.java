package ua.edu.sumdu.volonteerProject.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import ua.edu.sumdu.volonteerProject.security.JwtUserDetails;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Authority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String authority;

    @ManyToMany
    @JoinTable(name = "User_Authorities",
    joinColumns = {@JoinColumn(name="authority_id", referencedColumnName = "id")},
    inverseJoinColumns = {@JoinColumn(name="user_id", referencedColumnName = "id")})
    private List<JwtUserDetails> userDetails;


    @Override
    public String getAuthority() {
        return authority;
    }
}
