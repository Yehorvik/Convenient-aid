package ua.edu.sumdu.volonteerProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LastPollAndSendCityChecker {

    @Id
    private String cityName;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "city_name")
    @MapsId
    @Lazy
    private City city;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private Timestamp dateOfLastPolling;
    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private Timestamp dateOfLastSendingLocation;

}
