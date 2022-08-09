package ua.edu.sumdu.volonteerProject.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.sumdu.volonteerProject.model.UserVotes;

import java.util.UUID;

public interface UserVotesRepository extends JpaRepository<UserVotes, UUID>
{


}
