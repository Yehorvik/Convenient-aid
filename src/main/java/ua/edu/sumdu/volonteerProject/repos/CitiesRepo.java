package ua.edu.sumdu.volonteerProject.repos;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.sumdu.volonteerProject.model.City;

import java.util.List;
import java.util.Optional;

public interface CitiesRepo extends JpaRepository<City, String> {
    @Override
    <S extends City> List<S> findAll(Example<S> example);

    @Override
    Optional<City> findById(String name);
}
