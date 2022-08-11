package ua.edu.sumdu.volonteerProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.UUID;


@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserVote {
    @Id
    @GeneratedValue
    private UUID voteId;

    @Column(nullable = false)
    Date dateOfAnswer;

    @JoinColumn(referencedColumnName = "user_id", name = "user_id")
    @ManyToOne
    ChatLocation chatLocation;

}
