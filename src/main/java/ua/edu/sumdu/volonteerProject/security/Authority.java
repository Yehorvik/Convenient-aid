package ua.edu.sumdu.volonteerProject.security;

import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;
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

    @Column(unique = true)
    private String authority;

    @ManyToMany(mappedBy = "authorityList")//, fetch = FetchType.EAGER)
    private List<JwtUserDetails> userDetails;

}
