package ua.edu.sumdu.volonteerProject.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.sql.Date;

@NoArgsConstructor
@Data
public class UserDTO {
    @NotBlank(message = "username cant be blank!")
    @Email(message = "username should be an email")
    private String username;

    @NotBlank
    private String password;

    @NotBlank(message = "please, enter your first name")
    private String firstName;

    @NotBlank(message = "please, enter your second name")
    private String secondName;

    @NotBlank(message = "please, repeat your password")
    private String repeatPassword;

    @Column
    private String cityName;
}
