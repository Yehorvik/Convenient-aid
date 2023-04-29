package ua.edu.sumdu.volonteerProject.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SendLocationsDetails {

    private String adminUsername;
    @CreationTimestamp
    private Date createdAt;
    private long amountOfPoints;
    private String city;
    private Timestamp aprxDateOfDelivering;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;
}

