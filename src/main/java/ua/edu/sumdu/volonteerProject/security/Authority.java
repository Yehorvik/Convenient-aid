package ua.edu.sumdu.volonteerProject.security;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;
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
    @JsonBackReference
    private List<JwtUserDetails> userDetails;

}
