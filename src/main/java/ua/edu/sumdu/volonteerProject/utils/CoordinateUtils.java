package ua.edu.sumdu.volonteerProject.utils;

import org.springframework.data.rest.core.annotation.Description;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;

import static java.lang.Math.*;

public class CoordinateUtils {

    private final static int R = 1000*6371;
    /*
    *  method for calculating area by simplified formula
    *  sqrt( (lat1-lat2)^2 + cos((lat1+lat2)/2)*(lon1-lon2)^2 )
    *
    * */
    public static double calculateDistance(LocationCoordinates a, LocationCoordinates b){
        if(a == null || b == null){
            throw  new NullPointerException("Neither coordinates of a nor b can be a null");
        }
        double eLat = toRadians(a.getLatitude());
        double eLon = toRadians(a.getLongitude());
        double mLat = toRadians(b.getLatitude());
        double mLon = toRadians(b.getLongitude());
        return sqrt
                (
                        (mLat-eLat)*(mLat-eLat)
                                +
                                (eLon-mLon)*(eLon-mLon)*cos((eLat+mLat)/2)
                )*6371;
    }
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        // taken from http://rosettacode.org/wiki/Haversine_formula#Java
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        // R is the radius of the earth, set above statically
        return R * c;
    }

    public static double haversineDistance(LocationCoordinates point1, LocationCoordinates point2) {
        return haversineDistance(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(), point2.getLongitude());
    }

}
