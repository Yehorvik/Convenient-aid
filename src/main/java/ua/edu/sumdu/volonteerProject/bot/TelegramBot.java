package ua.edu.sumdu.volonteerProject.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ua.edu.sumdu.volonteerProject.errors.TelegramSendMessageError;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Component
public class TelegramBot {
    @Value("aiogram.api.bot.token")
    String token;
    private Client client;

    private WebTarget webTarget;

    @PostConstruct
    void init(){
        client = ClientBuilder.newClient();//.connectTimeout(3, TimeUnit.SECONDS);
        WebTarget webTarget = client.target("http:://api.telegram.org/bot{token}")
                .resolveTemplate("token", this.token);
    }

    public void sendMessagesWithInlineBrd(List<Long> chat_ids , String message, String replyText) throws TelegramSendMessageError {
        try {
            for (long a : chat_ids) {
                webTarget.path("sendMessage")
                        .queryParam("chat_id", a)
                        .queryParam("text", message)
                        .queryParam("reply_markup", "{InlineKeyboardButton:{text:"+replyText+"}}")
                        .request()
                        .get();
            }
        }catch(Exception e){
            throw new TelegramSendMessageError("cant send the message", e);
        }
    }

    public void sendMessage(List<Long> chat_ids , String message) throws TelegramSendMessageError {
        try {
            for (long a : chat_ids) {
                webTarget.path("sendMessage")
                        .queryParam("chat_id", a)
                        .queryParam("text", message)
                        .request()
                        .get();
            }
        }catch(Exception e){
            throw new TelegramSendMessageError("cant send the message", e);
        }
    }


    public void sendLocations(Map<Long, LocationCoordinates> usersAndLocations) throws TelegramSendMessageError {
        try {
            for (Map.Entry<Long, LocationCoordinates> entry : usersAndLocations.entrySet()) {
                Response response = webTarget.path("sendMessage")
                        .queryParam("chat_id", entry.getKey())
                        .queryParam("longitude", entry.getValue().getLongitude())
                        .queryParam("latitude", entry.getValue().getLatitude())
                        .request()
                        .get();
            }
        }catch(Exception e){
            throw new TelegramSendMessageError("cant send the message", e);
        }
    }

    @PreDestroy
    private void closeClient(){
        client.close();
    }

}
