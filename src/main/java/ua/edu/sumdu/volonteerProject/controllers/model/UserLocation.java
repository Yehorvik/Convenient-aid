package ua.edu.sumdu.volonteerProject.controllers.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLocation {

    @OneToOne
    @JoinColumn(name = "city_name", referencedColumnName = "name")
    private City cityName;

    @Id
    @Column
    private UUID id;

    @Embedded
    private LocationCoordinates locationCoordinates;

    private long userId;
}
