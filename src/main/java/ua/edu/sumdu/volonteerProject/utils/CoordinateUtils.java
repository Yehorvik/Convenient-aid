package ua.edu.sumdu.volonteerProject.utils;

import org.springframework.data.rest.core.annotation.Description;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;

import static java.lang.Math.*;

public class CoordinateUtils {
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
        double result = sqrt
                (
                        (mLat-eLat)*(mLat-eLat)
                                +
                                (eLon-mLon)*(eLon-mLon)*cos((eLat+mLat)/2)
                )*6371;
        return result;
    }
}
