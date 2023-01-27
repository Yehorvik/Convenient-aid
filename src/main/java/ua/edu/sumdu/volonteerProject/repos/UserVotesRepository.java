package ua.edu.sumdu.volonteerProject.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.UserVote;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface UserVotesRepository extends JpaRepository<UserVote, UUID>
{
    public List<UserVote> getUserVotesByDateOfAnswerGreaterThanAndChatLocation_CityName(Date date, City city);
    public List<UserVote> getUserVotesByActiveAndChatLocation_CityName(boolean active, City city);
    @Modifying
    @Query(value ="update UserVote u set u.active = false where u.active = true and u.chatLocation.chatId in (select c.chatId from ChatLocation c where c.cityName = ?1)")
    public void inactivateUserVoteByCity(City city);
}
