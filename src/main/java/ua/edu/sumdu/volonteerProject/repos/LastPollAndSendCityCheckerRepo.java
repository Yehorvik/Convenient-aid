package ua.edu.sumdu.volonteerProject.repos;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LastPollAndSendCityChecker;

public interface LastPollAndSendCityCheckerRepo extends JpaRepository<LastPollAndSendCityChecker,City> {
    LastPollAndSendCityChecker findLastPollAndSendCityCheckerByCity_Name(String cityName);
    @Modifying
    @Query("update LastPollAndSendCityChecker set dateOfLastSendingLocation = current_timestamp where city.name = ?1 ")
    int updateSendDateByCity(String cityName);
    @Modifying
    @Query("update LastPollAndSendCityChecker set dateOfLastPolling = current_timestamp where city.name = ?1 ")
    int updatePollDateByCity(String cityName);
}
