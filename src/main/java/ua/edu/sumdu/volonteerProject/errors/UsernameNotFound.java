package ua.edu.sumdu.volonteerProject.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UsernameNotFound {
    private String message;
}
