package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.TimeOfAid;
import ua.edu.sumdu.volonteerProject.repos.TimeOfAidRepository;
import ua.edu.sumdu.volonteerProject.services.CityService;
import ua.edu.sumdu.volonteerProject.services.DeliveryTimerService;

import java.sql.Time;
import java.sql.Timestamp;

@Service
@Slf4j
@AllArgsConstructor
public class DeliveryTimerServiceImpl implements DeliveryTimerService {

    private final CityService cityService;
    private final TimeOfAidRepository timeOfAidRepository;



    @Override
    public void updateNextTimeDelivery(City city, Timestamp nextDeliveryTime) {
        TimeOfAid time = new TimeOfAid(city.getName(),city,nextDeliveryTime);
        log.info("the next time delivery should be saved with next params: " + time.toString());
        timeOfAidRepository.save(time);
    }
}
