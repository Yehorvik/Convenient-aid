package ua.edu.sumdu.volonteerProject.services;

import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.model.SendLocationsDetails;

public interface LogHistoryService {
    public void LogLocationSending(SendLocationsDetails sendLocationsDetails);
}
