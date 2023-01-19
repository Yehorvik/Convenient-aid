package ua.edu.sumdu.volonteerProject.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.model.SendLocationsDetails;
import ua.edu.sumdu.volonteerProject.repos.SendLocationDetailsRepo;
import ua.edu.sumdu.volonteerProject.services.LogHistoryService;

@Service
public class LogHistoryServiceImpl implements LogHistoryService {
    @Autowired
    SendLocationDetailsRepo sendLocationDetailsRepo;


    @Override
    public void LogLocationSending(SendLocationsDetails sendLocationsDetails) {
        sendLocationDetailsRepo.save(sendLocationsDetails);
    }
}
