package ua.edu.sumdu.volonteerProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.util.UUID;


@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long voteId;

    @Column(nullable = false, columnDefinition = "date default CURRENT_DATE")
    Date dateOfAnswer;

    @JoinColumn(referencedColumnName = "user_id", name = "user_id", nullable = false)
    @ManyToOne
    ChatLocation chatLocation;

}
