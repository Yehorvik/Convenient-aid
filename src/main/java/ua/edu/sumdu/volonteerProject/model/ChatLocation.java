package ua.edu.sumdu.volonteerProject.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatLocation {

    @ManyToOne
    @JoinColumn(name = "city_name", referencedColumnName = "name")
    private City cityName;

    private boolean hasPollInvitation;

    @Embedded
    private LocationCoordinates locationCoordinates;



    @Id
    @Column(name = "user_id")
    private long chatId;
}
