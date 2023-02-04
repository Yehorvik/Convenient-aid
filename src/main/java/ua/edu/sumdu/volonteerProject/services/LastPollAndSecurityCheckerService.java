package ua.edu.sumdu.volonteerProject.services;

import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LastPollAndSendCityChecker;

import java.sql.Timestamp;
import java.util.Date;

public interface LastPollAndSecurityCheckerService {
    public long getLastPollingExpirationDifferance(City city);
    public long getLastSendMessageExpirationDifferance(City city);
}
