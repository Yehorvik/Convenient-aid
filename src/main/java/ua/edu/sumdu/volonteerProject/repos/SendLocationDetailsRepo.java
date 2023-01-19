package ua.edu.sumdu.volonteerProject.repos;

import org.springframework.data.repository.CrudRepository;
import ua.edu.sumdu.volonteerProject.model.SendLocationsDetails;

public interface SendLocationDetailsRepo extends CrudRepository<SendLocationsDetails, Long> {
    @Override
    <S extends SendLocationsDetails> S save(S entity);

    @Override
    Iterable<SendLocationsDetails> findAll();
}
