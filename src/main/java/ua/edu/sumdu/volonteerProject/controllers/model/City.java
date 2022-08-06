package ua.edu.sumdu.volonteerProject.controllers.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class City {
    @Id
    private String name;


    private double area;
}
