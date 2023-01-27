package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.bot.TelegramBot;
import ua.edu.sumdu.volonteerProject.errors.TelegramSendMessageError;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.ChatLocation;
import ua.edu.sumdu.volonteerProject.model.UserVote;
import ua.edu.sumdu.volonteerProject.repos.CitiesRepo;
import ua.edu.sumdu.volonteerProject.repos.ChatLocationRepository;
import ua.edu.sumdu.volonteerProject.repos.UserVotesRepository;
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
    private final ChatLocationRepository chatLocationRepository;
    private final CitiesRepo citiesRepo;
    private final UserVotesRepository userVotesRepository;

    private final String MESSAGE = "Do you want to participate in the next volonteers poll?";
    private final String REPLY_MESSAGE = "YES!";

    private TelegramBot telegramBot;

//    public void pushMessagesToUsers(City city){
//        citiesRepo.findById(city.getName()).orElseThrow(() -> new NullPointerException("city doesnt exist"));
//        List<ChatLocation> chatLocations = chatLocationRepository.findByCityName(city);
//        for(ChatLocation chatLocation: chatLocations){
//            chatLocation
//        }
//    }

    @Transactional
    @Override
    public void pushMessagesToUsers(City city, List<LocationCoordinates> locationCoordinates) throws TelegramSendMessageError {
        List<UserVote> chatLocations =  userVotesRepository.getUserVotesByActiveAndChatLocation_CityName(true,city);
        Map<Long, LocationCoordinates> chatsAndLocations = new HashMap<>();
        System.out.println(chatLocations);
        System.out.println(locationCoordinates);
        chatLocations.stream().parallel().forEach(e -> {

            chatsAndLocations.put(e.getChatLocation().getChatId(), locationCoordinates.stream().min((a, b) -> {
                if(a.equals(b)){
                    return 0;
                }
                return
                    (CoordinateUtils.calculateDistance(e.getChatLocation().getLocationCoordinates(), a) - CoordinateUtils.calculateDistance(e.getChatLocation().getLocationCoordinates(), b))<=0?-1:1;}).orElse(null));
            });
        telegramBot.sendLocations(chatsAndLocations);
        chatLocationRepository.setHasInvitedToFalseForVotedLocationsByCity(city);
        userVotesRepository.inactivateUserVoteByCity(city);
    }

    @Transactional
    @Override
    public void createPoll(City city) throws TelegramSendMessageError {
        List<Long> doesNotAnsweredThePrevPollUsers = chatLocationRepository.getChatIdBy(city, true);
        List<Long> answeredThePrevPollUsers = chatLocationRepository.getChatIdBy(city, false);
        telegramBot.sendMessagesWithInlineBrd(answeredThePrevPollUsers,MESSAGE,REPLY_MESSAGE);
        telegramBot.sendMessage(doesNotAnsweredThePrevPollUsers, "You didn`t answer the in the previous poll, the next one has been startred, so you can vote this time, using the previous button!");

        chatLocationRepository.setHasInvitedToFalse(city,true);

    }
}
