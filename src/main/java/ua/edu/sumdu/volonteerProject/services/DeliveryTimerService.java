package ua.edu.sumdu.volonteerProject.services;

import ua.edu.sumdu.volonteerProject.model.City;

import java.sql.Timestamp;

public interface DeliveryTimerService {
    void updateNextTimeDelivery(City city, Timestamp nextDeliveryTime);
}
