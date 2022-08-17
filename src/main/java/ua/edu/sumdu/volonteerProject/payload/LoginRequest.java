package ua.edu.sumdu.volonteerProject.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username cant be blank")
    private String username;
    @NotBlank(message = "Password cant be blank")
    private CharSequence password;
}
