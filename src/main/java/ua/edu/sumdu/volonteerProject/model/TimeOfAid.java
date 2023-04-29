package ua.edu.sumdu.volonteerProject.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Data
public class TimeOfAid implements Persistable<String> {
    @Id
            @Column(name = "city_id")
    private String cityName;

    public TimeOfAid(String cityName, City city, Timestamp time) {
        this.cityName = cityName;
        this.city = city;
        this.time = time;
    }

    @OneToOne
    @JoinColumn(name = "city_id")
    //@NotFound(action = NotFoundAction.IGNORE)
    private City city;
    @Column(nullable = false)
    private Timestamp time;

    @Transient
    private boolean isNew = true;

    @Override
    public String getId() {
        return cityName;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
