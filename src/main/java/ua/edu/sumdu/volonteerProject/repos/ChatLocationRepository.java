package ua.edu.sumdu.volonteerProject.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.edu.sumdu.volonteerProject.model.ChatLocation;
import ua.edu.sumdu.volonteerProject.model.City;

import java.util.List;
import java.util.UUID;

public interface ChatLocationRepository extends JpaRepository<ChatLocation, Long> {
    public  List<ChatLocation> findByCityName(City name);

    @Query("select c.chatId from ChatLocation c where c.hasPollInvitation = ?2 and c.cityName = ?1")
    public List<Long> getChatIdBy(City city , Boolean hasPollInvitation);

    @Modifying
    @Query("update ChatLocation c set c.hasPollInvitation=false where c.chatId in (select r.chatLocation.chatId from UserVote r where r.chatLocation.cityName = ?1 and r.active = true)")
    public void setHasInvitedToFalseForVotedLocationsByCity(City city);

    @Modifying
    @Query("update ChatLocation c set c.hasPollInvitation = ?2 where c.cityName = ?1")
    public void setHasInvitedToFalse(City city, boolean status);

    public List<ChatLocation> findAllByCityNameAndHasPollInvitation(City name,boolean hasPollInvitation);
}
