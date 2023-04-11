package ua.edu.sumdu.volonteerProject.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.edu.sumdu.volonteerProject.model.City;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtUserDetails implements UserDetails {


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "User_Authorities",
            inverseJoinColumns = {@JoinColumn(name="authority_id", referencedColumnName = "id")},
            joinColumns = {@JoinColumn(name="user_id", referencedColumnName = "id")})
    private Set<Authority> authorityList;

    @ManyToOne
    @JoinColumn(name = "city_name", referencedColumnName = "name")
    private City city;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String secondName;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;


    private boolean isBlocked;

    public void addAuthority(Authority authority){
        this.authorityList.add(authority);
    }

    @Override
    public Collection< ? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isBlocked;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isBlocked;
    }

    @Override
    public boolean isEnabled() {
        return !isBlocked;
    }
}
