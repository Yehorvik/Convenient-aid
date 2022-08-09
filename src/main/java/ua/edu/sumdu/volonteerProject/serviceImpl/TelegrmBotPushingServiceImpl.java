package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.bot.TelegramBot;
import ua.edu.sumdu.volonteerProject.errors.TelegramSendMessageError;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.ChatLocation;
import ua.edu.sumdu.volonteerProject.repos.CitiesRepo;
import ua.edu.sumdu.volonteerProject.repos.UserLocationRepository;
import ua.edu.sumdu.volonteerProject.services.TelegramBotPushingService;
import ua.edu.sumdu.volonteerProject.utils.CoordinateUtils;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TelegrmBotPushingServiceImpl implements TelegramBotPushingService {
    private final UserLocationRepository userLocationRepository;
    private final CitiesRepo citiesRepo;

    private final String MESSAGE = "Do you want to participate in the next volonteers poll?";

    private TelegramBot telegramBot;

    public void pushMessagesToUsers(City city){
        citiesRepo.findById(city.getName()).orElseThrow(() -> new NullPointerException("city doesnt exist"));
        userLocationRepository.findByCityName(city.getName());
    }

    @Transactional
    @Override
    public void pushMessagesToUsers(City city, List<LocationCoordinates> locationCoordinates) throws TelegramSendMessageError {
        List<ChatLocation> chatLocations =  userLocationRepository.findByCityName(city.getName());
        Map<Long, LocationCoordinates> chatsAndLocations = new HashMap<>();
        chatLocations.stream().parallel().forEach(e -> {

            chatsAndLocations.put(e.getChatId(), locationCoordinates.stream().min((a, b) -> {
                return
                    (CoordinateUtils.calculateDistance(e.getLocationCoordinates(), a) - CoordinateUtils.calculateDistance(e.getLocationCoordinates(), b))<0?-1:1;}).orElse(null));
            });
        telegramBot.sendLocations(chatsAndLocations);
    }

    @Transactional
    @Override
    public void createPoll(City city) throws TelegramSendMessageError {
        List<ChatLocation> chatLocations =  userLocationRepository.findByCityName(city.getName());
        List<Long> ids = chatLocations.stream().map(e->e.getChatId()).collect(Collectors.toUnmodifiableList());
        telegramBot.sendMessage(ids,MESSAGE);
    }
}
