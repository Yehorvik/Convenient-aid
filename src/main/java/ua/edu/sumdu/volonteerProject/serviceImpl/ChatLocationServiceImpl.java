package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.model.ChatLocation;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.repos.ChatLocationRepository;
import ua.edu.sumdu.volonteerProject.services.ChatLocationService;

import java.util.List;

@AllArgsConstructor
@Service
public class ChatLocationServiceImpl implements ChatLocationService {
    private final ChatLocationRepository chatLocationRepository;


    @Override
    public List<ChatLocation> getChatsByCity(City city) {
        return chatLocationRepository.findByCityName(city);
    }
}
