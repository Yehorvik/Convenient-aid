package ua.edu.sumdu.volonteerProject.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;

@Data
@AllArgsConstructor
public class CityDTO {
    private LocationCoordinates locationCoordinates;
    private String name;
    private double area;
}
