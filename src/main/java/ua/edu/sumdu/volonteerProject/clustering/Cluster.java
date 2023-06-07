package ua.edu.sumdu.volonteerProject.clustering;

import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Cluster extends LocationCoordinates {
    private Set<LocationCoordinates> locationCoordinatesSet = new HashSet<>();

    public void clearPoints(){
        locationCoordinatesSet = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Cluster cluster = (Cluster) o;
        return locationCoordinatesSet.equals(cluster.locationCoordinatesSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), locationCoordinatesSet);
    }

    public Set<LocationCoordinates> getLocationCoordinatesSet() {
        return locationCoordinatesSet;
    }

    public void setLocationCoordinatesSet(Set<LocationCoordinates> locationCoordinatesSet) {
        this.locationCoordinatesSet = locationCoordinatesSet;
    }

    public Cluster(double longitude, double latitude) {
        super(longitude, latitude);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Cluster clone = new Cluster(this.getLongitude(), this.getLatitude());
        clone.locationCoordinatesSet = locationCoordinatesSet.stream().collect(Collectors.toSet());
        return clone;
    }
}
