package ua.edu.sumdu.volonteerProject.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JwtLoginSuccessResponse
{
    private boolean success;
    private String token;
}
