package ua.edu.sumdu.volonteerProject;

import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import ua.edu.sumdu.volonteerProject.model.ChatLocation;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.model.UserVote;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class TestingForLargeAmountUtil {

    Connection connection;

    public void initDataBase() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=creniva33&ssl=false");

    }

    public void insertRandomSumyData() throws SQLException {
        Random random = new Random();
        Set<Long> uniChats = new TreeSet<>() ;
        for(int i = 0; i < 140000; i++) {
            uniChats.add(5_000_000_0 + random.nextLong(140000));
        }
        LocationCoordinates cityCoordinates = new LocationCoordinates();
        cityCoordinates.setLatitude(50.9216
        );
        cityCoordinates.setLongitude(34.80029);
        List<Long> chatList = uniChats.stream().toList();

        List<ChatLocation> chatLocations = chatList.stream().map( e -> new ChatLocation(  new City("Sumy",cityCoordinates,90.20),
                true,
                new LocationCoordinates(
                        random.nextGaussian(cityCoordinates.getLongitude(),0.004),
                        random.nextGaussian(cityCoordinates.getLatitude(),0.004)
                ),
                e.longValue())).collect(Collectors.toList());

        List<UserVote> v = chatLocations.stream().map(e-> new UserVote(0,null, true, e)).collect(Collectors.toList());
        PreparedStatement p = connection.prepareStatement("insert into chat_location(user_id, latitude, longitude, city_name,has_poll_invitation) values(?, ?,?,?,?)");
        PreparedStatement p2 = connection.prepareStatement("insert into user_vote(vote_id, date_of_answer, user_id) values(default, default, ?)");
        chatLocations.stream().forEach(e -> {
            try {
                p.setLong(1,e.getChatId());
                p.setDouble(2, e.getLocationCoordinates().getLatitude());
                p.setDouble(3, e.getLocationCoordinates().getLongitude());
                p.setString(4, e.getCityName().getName());
                p.setBoolean(5, e.isHasPollInvitation());
                p.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        v.stream().forEach(e -> {
            try {
                p2.setLong(1,e.getChatLocation().getChatId());
                p2.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    public  void closeDataBase() throws SQLException {
        connection.close();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        TestingForLargeAmountUtil testingForLargeAmountUtil = new TestingForLargeAmountUtil();
        testingForLargeAmountUtil.initDataBase();
        testingForLargeAmountUtil.insertRandomSumyData();
        testingForLargeAmountUtil.closeDataBase();
    }

}
