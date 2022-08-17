package ua.edu.sumdu.volonteerProject.errors;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class InvalidLoginResponse {
    private String username;
    private String password;

    public InvalidLoginResponse(){
        username = "invalid username";
        password = "invalid password";
    }
}
