package ua.edu.sumdu.volonteerProject.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ua.edu.sumdu.volonteerProject.errors.TelegramSendMessageError;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.telegram.CallbackTelegramButtonEntity;
import ua.edu.sumdu.volonteerProject.model.telegram.MessageEntity;
import ua.edu.sumdu.volonteerProject.model.telegram.TelegramReplyMarkup;
import ua.edu.sumdu.volonteerProject.utils.SemaphoreRateLimiter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationHandler;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
@Slf4j
public class TelegramBot {
    @Value("${aiogram.api.bot.token}")
    String token;
    private Client client;

    private WebTarget webTarget;

    private ExecutorService executorService;

    @PostConstruct
    void init(){
        executorService = Executors.newFixedThreadPool(40);
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).setConnectionTimeToLive(10000, TimeUnit.MILLISECONDS).build();
        poolingHttpClientConnectionManager.setMaxTotal(500);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(100);

        ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
        client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).asyncExecutor(executorService).httpEngine(engine).build();//.connectTimeout(3, TimeUnit.SECONDS);
        webTarget = client.target("https://api.telegram.org/bot{token}")
                .resolveTemplate("token", this.token);
        System.out.println(token);
    }

    public Map<Long, Boolean> sendMessagesWithInlineBrd(List<Long> chat_ids , String message, String replyText) throws TelegramSendMessageError {
        try {
            List<CallbackTelegramButtonEntity> buttonEntityList = new ArrayList<>();
            List<List<CallbackTelegramButtonEntity>> keyboardList = new ArrayList<>();
            buttonEntityList.add(new CallbackTelegramButtonEntity(replyText, "/participateInPoll"));
            keyboardList.add(buttonEntityList);
            CountDownLatch countDownLatch = new CountDownLatch(chat_ids.size());
            SemaphoreRateLimiter semaphoreRateLimiter = SemaphoreRateLimiter.create(30, TimeUnit.SECONDS);
            Map<Long, Boolean> failed =new HashMap<>();
            TelegramReplyMarkup telegramReplyMarkup = new TelegramReplyMarkup(keyboardList);
            for (long a : chat_ids) {
                //System.out.println(a);
                while (!semaphoreRateLimiter.tryAcquire()){
                    Thread.sleep(200);
                }
                MessageEntity messageEntity = new MessageEntity(message, a, telegramReplyMarkup);
                //.queryParam("reply_markup", "{InlineKeyboardButton:{text:"+replyText+"}}")
                ObjectMapper objectMapper =new JsonMapper();
                String returnString = objectMapper.writeValueAsString(messageEntity);
                //System.out.println(returnString);
                webTarget.path("sendMessage")
                        //.queryParam("chat_id", a)
                        //.queryParam("text", message)
                        //.queryParam("reply_markup", "{InlineKeyboardButton:{text:"+replyText+"}}")
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .async()
                        .post(Entity.entity(returnString,MediaType.APPLICATION_JSON_TYPE),new InvocationCallback<Response>() {
                            @Override
                            public void completed(Response response) {
                                countDownLatch.countDown();
                                if(response.getStatus() >=400){
                                    failed.put(a, false);
                                    log.info(String.valueOf(response.getStatus()));
                                    response.close();
                                    throw new RuntimeException();
                                }
                                response.close();
                                //                       semaphore.release();
                            }

                            @Override
                            public void failed(Throwable throwable) {
                                countDownLatch.countDown();
                                throwable.printStackTrace();
                                //                     semaphore.release();
                                log.error("error occured but passed "+ Thread.currentThread());
                                failed.put(a, false);
                                try {
                                    throw new Exception();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        })
                ;
            }
            countDownLatch.await();
            return failed;
        }catch(Exception e){
            throw new TelegramSendMessageError("cant send the message", e);
        }
    }

    public Map<Long, Boolean> sendMessage(List<Long> chat_ids , String message) throws TelegramSendMessageError {
        CountDownLatch countDownLatch = new CountDownLatch(chat_ids.size());
        SemaphoreRateLimiter semaphoreRateLimiter = SemaphoreRateLimiter.create(30, TimeUnit.SECONDS);
        Map<Long, Boolean> failed =new HashMap<>();
        try {
            for (long a : chat_ids) {
                while(!semaphoreRateLimiter.tryAcquire()){
                    Thread.sleep(200);
                }
                webTarget.path("sendMessage")
                        .queryParam("chat_id", a)
                        .queryParam("text", message)
                        .request()
                        .async().get(new InvocationCallback<Response>() {
                            @Override
                            public void completed(Response response) {
                                countDownLatch.countDown();
                                if(response.getStatus() >=400){
                                    failed.put(a, false);
                                    log.info(String.valueOf(response.getStatus()));
                                    response.close();
                                    throw new RuntimeException();
                                }
                                response.close();
                                //                       semaphore.release();
                            }

                            @Override
                            public void failed(Throwable throwable) {
                                countDownLatch.countDown();
                                throwable.printStackTrace();
                                //                     semaphore.release();
                                log.error("error occured but passed "+ Thread.currentThread());
                                failed.put(a, false);
                                try {
                                    throw new Exception();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        })
                        ;
            }
            if(!countDownLatch.await(chat_ids.size()/30+4,TimeUnit.SECONDS)){
                throw new TimeoutException();
            }
            return failed;
        }catch(Exception e){
            throw new TelegramSendMessageError("cant send the message", e);
        }

    }


    public Map<Long, Boolean> sendLocations(Map<Long, LocationCoordinates> usersAndLocations) throws TelegramSendMessageError {
        try {
            Map<Long, Boolean> failed =new HashMap<>();
            CountDownLatch countDownLatch = new CountDownLatch(usersAndLocations.size());
            SemaphoreRateLimiter semaphoreRateLimiter = SemaphoreRateLimiter.create(30, TimeUnit.SECONDS);
            for (Map.Entry<Long, LocationCoordinates> entry : usersAndLocations.entrySet()) {
               // semaphore.acquire();
                while(!semaphoreRateLimiter.tryAcquire()){
                    Thread.sleep(200);
                }
                webTarget.path("sendLocation")
                        .queryParam("chat_id", entry.getKey())
                        .queryParam("protect_content", true)
                        .queryParam("longitude", entry.getValue().getLongitude())
                        .queryParam("latitude", entry.getValue().getLatitude())
                        .request()
                        .async().get(
                                new InvocationCallback<Response>() {
                                    @Override
                                    public void completed(Response response) {
                                        countDownLatch.countDown();
                                        if(response.getStatus() >=400){
                                            failed.put(entry.getKey(), false);
                                            log.info(String.valueOf(response.getStatus()));
                                            response.close();
                                            throw new RuntimeException();
                                        }
                                        response.close();
                                        //                       semaphore.release();
                                    }

                                    @Override
                                    public void failed(Throwable throwable) {
                                        countDownLatch.countDown();
                                        throwable.printStackTrace();
                                        //                     semaphore.release();
                                        log.error("error occured but passed "+ Thread.currentThread());
                                        failed.put(entry.getKey(), false);
                                        try {
                                            throw new Exception();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }

                                }
                        )
                        ;

            }
            countDownLatch.await();
            log.info("somehow messages have been send");
            return failed;
        }catch(Exception e){
            throw new TelegramSendMessageError("cant send the message", e);
        }
    }

    @PreDestroy
    private void closeClient(){
        client.close();
        executorService.shutdown();
    }

}
