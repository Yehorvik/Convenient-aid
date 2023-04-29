package ua.edu.sumdu.volonteerProject.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.edu.sumdu.volonteerProject.security.Authority;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
public class UserInfoDTO {
    private String username;

    private String firstName;

    private String secondName;

    private UUID id;

    private List<Authority> roles;

    private boolean isBlocked;

    @Column
    private String cityName;
}
