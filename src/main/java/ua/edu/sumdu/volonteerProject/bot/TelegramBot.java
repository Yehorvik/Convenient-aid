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
import java.util.*;
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
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).setConnectionTimeToLive(1, TimeUnit.NANOSECONDS).build();
        poolingHttpClientConnectionManager.setMaxTotal(500);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(100);

        ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
        client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).asyncExecutor(executorService).httpEngine(engine).property("http.connection.timeout", 1).property("http.receive.timeout", 1).build();//.connectTimeout(3, TimeUnit.SECONDS);
        webTarget = client.target("https://api.telegram.org/bot{token}")
                .resolveTemplate("token", this.token);
    }

    public Set<Long> sendMessagesWithInlineBrd(List<Long> chat_ids , String message, String replyText) throws TelegramSendMessageError {
        try {
            List<CallbackTelegramButtonEntity> buttonEntityList = new ArrayList<>();
            List<List<CallbackTelegramButtonEntity>> keyboardList = new ArrayList<>();
            buttonEntityList.add(new CallbackTelegramButtonEntity(replyText, "/participateInPoll"));
            keyboardList.add(buttonEntityList);
            CountDownLatch countDownLatch = new CountDownLatch(chat_ids.size());
            SemaphoreRateLimiter semaphoreRateLimiter = SemaphoreRateLimiter.create(30, TimeUnit.SECONDS);
            Set<Long> failed =new HashSet<>();
            TelegramReplyMarkup telegramReplyMarkup = new TelegramReplyMarkup(keyboardList);
            for (long a : chat_ids) {
                while (!semaphoreRateLimiter.tryAcquire()){
                    Thread.sleep(200);
                }
                MessageEntity messageEntity = new MessageEntity(message, a, telegramReplyMarkup);
                ObjectMapper objectMapper =new JsonMapper();
                String returnString = objectMapper.writeValueAsString(messageEntity);
                webTarget.path("sendMessage")
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .async()
                        .post(Entity.entity(returnString,MediaType.APPLICATION_JSON_TYPE),new InvocationCallback<Response>() {
                            @Override
                            public void completed(Response response) {
                                countDownLatch.countDown();
                                if(response.getStatus() >=400){
                                    failed.add(a);
                                    log.debug(String.valueOf(response.getStatus() + " count down latch " + countDownLatch.getCount() + " "));
                                    response.close();
                                    throw new RuntimeException();
                                }
                                response.close();
                            }

                            @Override
                            public void failed(Throwable throwable) {
                                countDownLatch.countDown();
                                throwable.printStackTrace();
                                log.error("error occured but passed "+ Thread.currentThread());
                                failed.add(a);
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
            log.info("messages have been send: number of errors : " + failed.size() + " number of all requests: " + chat_ids.size());
            return failed;
        }catch(Exception e){
            throw new TelegramSendMessageError("cant send the message", e);
        }
    }

    public Set<Long> sendMessage(List<Long> chat_ids , String message) throws TelegramSendMessageError {
        CountDownLatch countDownLatch = new CountDownLatch(chat_ids.size());
        SemaphoreRateLimiter semaphoreRateLimiter = SemaphoreRateLimiter.create(30, TimeUnit.SECONDS);
        Set<Long> failed =new HashSet<>();
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
                                    failed.add(a);
                                    log.debug("failed request: " + String.valueOf(response.getStatus() + " count down latch " + countDownLatch.getCount() + " "));
                                    response.close();
                                    throw new RuntimeException();
                                }
                                response.close();
                            }

                            @Override
                            public void failed(Throwable throwable) {
                                countDownLatch.countDown();
                                throwable.printStackTrace();
                                log.error("error occured but passed "+ Thread.currentThread());
                                failed.add(a);
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
            log.info("messages have been send: number of errors : " + failed.size() + " number of all requests: " + chat_ids.size());
            return failed;
        }catch(Exception e){
            throw new TelegramSendMessageError("cant send the message", e);
        }

    }


    public Set<Long> sendLocations(Map<Long, LocationCoordinates> usersAndLocations) throws TelegramSendMessageError {
        try {
            Set<Long> failed =new HashSet<>();
            CountDownLatch countDownLatch = new CountDownLatch(usersAndLocations.size());
            SemaphoreRateLimiter semaphoreRateLimiter = SemaphoreRateLimiter.create(30, TimeUnit.SECONDS);
            for (Map.Entry<Long, LocationCoordinates> entry : usersAndLocations.entrySet()) {
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
                                            failed.add(entry.getKey());
                                            log.debug("failed request: " + String.valueOf(response.getStatus() + " count down latch " + countDownLatch.getCount() + " "));
                                            response.close();
                                            throw new RuntimeException();
                                        }
                                        response.close();
                                    }

                                    @Override
                                    public void failed(Throwable throwable) {
                                        countDownLatch.countDown();
                                        throwable.printStackTrace();
                                        log.error("error occured but passed "+ Thread.currentThread());
                                        failed.add(entry.getKey());
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
            log.info("messages have been send: number of errors : " + failed.size() + " number of all requests: " + usersAndLocations.size());
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
