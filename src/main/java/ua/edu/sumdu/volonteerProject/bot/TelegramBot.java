package ua.edu.sumdu.volonteerProject.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ua.edu.sumdu.volonteerProject.errors.TelegramSendMessageError;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.telegram.CallbackTelegramButtonEntity;
import ua.edu.sumdu.volonteerProject.model.telegram.MessageEntity;
import ua.edu.sumdu.volonteerProject.model.telegram.TelegramReplyMarkup;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class TelegramBot {
    @Value("${aiogram.api.bot.token}")
    String token;
    private Client client;

    private WebTarget webTarget;

    @PostConstruct
    void init(){
        client = ClientBuilder.newClient();//.connectTimeout(3, TimeUnit.SECONDS);
        webTarget = client.target("https://api.telegram.org/bot{token}")
                .resolveTemplate("token", this.token);
        System.out.println(token);
    }

    public void sendMessagesWithInlineBrd(List<Long> chat_ids , String message, String replyText) throws TelegramSendMessageError {
        try {
            List<CallbackTelegramButtonEntity> buttonEntityList = new ArrayList<>();
            List<List<CallbackTelegramButtonEntity>> keyboardList = new ArrayList<>();
            buttonEntityList.add(new CallbackTelegramButtonEntity(replyText, "/participateInPoll"));
            keyboardList.add(buttonEntityList);
            TelegramReplyMarkup telegramReplyMarkup = new TelegramReplyMarkup(keyboardList);
            for (long a : chat_ids) {
                System.out.println(a);
                MessageEntity messageEntity = new MessageEntity(message, a, telegramReplyMarkup);
                //.queryParam("reply_markup", "{InlineKeyboardButton:{text:"+replyText+"}}")
                ObjectMapper objectMapper =new JsonMapper();
                String returnString = objectMapper.writeValueAsString(messageEntity);
                System.out.println(returnString);
                webTarget.path("sendMessage")
                        //.queryParam("chat_id", a)
                        //.queryParam("text", message)
                        //.queryParam("reply_markup", "{InlineKeyboardButton:{text:"+replyText+"}}")
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(returnString,MediaType.APPLICATION_JSON_TYPE))
                        .close();
            }
        }catch(Exception e){
            throw new TelegramSendMessageError("cant send the message", e);
        }
    }

    public void sendMessage(List<Long> chat_ids , String message) throws TelegramSendMessageError {
        try {
            for (long a : chat_ids) {
                Response response = webTarget.path("sendMessage")
                        .queryParam("chat_id", a)
                        .queryParam("text", message)
                        .request()
                        .get();
                response.close();
            }
        }catch(Exception e){
            throw new TelegramSendMessageError("cant send the message", e);
        }
    }


    public void sendLocations(Map<Long, LocationCoordinates> usersAndLocations) throws TelegramSendMessageError {
        try {
            List<Future<Response>> responseList = new ArrayList<>();
            for (Map.Entry<Long, LocationCoordinates> entry : usersAndLocations.entrySet()) {
                responseList.add(webTarget.path("sendLocation")
                        .queryParam("chat_id", entry.getKey())
                        .queryParam("protect_content", true)
                        .queryParam("longitude", entry.getValue().getLongitude())
                        .queryParam("latitude", entry.getValue().getLatitude())
                        .request()
                        .async().get())
                        ;


            }
            for(Future<Response> responseFuture: responseList) {
                try {
                    responseFuture.get().close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new TelegramSendMessageError("cant send the message", e);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    throw new TelegramSendMessageError("cant send the message", e);
                }
            };
        }catch(Exception e){
            throw new TelegramSendMessageError("cant send the message", e);
        }
    }

    @PreDestroy
    private void closeClient(){
        client.close();
    }

}
