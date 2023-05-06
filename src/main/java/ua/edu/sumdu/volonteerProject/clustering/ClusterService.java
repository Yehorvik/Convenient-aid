package ua.edu.sumdu.volonteerProject.clustering;

import lombok.extern.slf4j.Slf4j;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.sumdu.volonteerProject.utils.CoordinateUtils.haversineDistance;

@Slf4j
public class ClusterService {


    // radius of the earth (approx), in meters
    public static final double R = 6372.8 * 1000;

    /**
     * Uses the KMeans algorithm to generate k clusters from the set of {@link LocationCoordinates}s. Will initialize
     * with a random set of k {@ Cluster}s
     *
     * @param points a List of {@link LocationCoordinates}s
     * @param k
     * @return
     */
    public static List<Cluster> cluster(List<LocationCoordinates> points, int k) {
        return cluster(points, k, buildRandomInitialClusters(points, k));
    }

    /**
     * Uses the KMeans algorithm to generate k clusters from the set of points using a predefined starting set of
     * {@link Cluster}
     *
     * @param points a List of {@link LocationCoordinates}
     * @param k
     * @param clusters
     * @return
     */
    public static List<Cluster> cluster(List<LocationCoordinates> points, int k, List<Cluster> clusters) {
        // adapted from http://datasciencelab.wordpress.com/2013/12/12/clustering-with-k-means-in-python/
        // Uses KMeans to converge the clusters on the points
        // Kmeans is an iterative, two step apporach:
        // 1. Assign each point to the closest centroid
        // 2. Recalculate each centroid to be the center of its assigned points
        // The algorithm is initialized with k random Centroids.
        // create empty old clusters
        List<Cluster> oldClusters = new ArrayList<>();
        for (Cluster e:
             clusters) {
            oldClusters.add(new Cluster(-180, -90));
        }
        int i = 0;
        while (!hasConverged(oldClusters, clusters)) {
            log.debug("On iteration " + i);
            i++;
            // grr... cloning in java / groovy is a bit tricky when it comes to lists
            // they're copied by reference, so in order for the converged
            oldClusters = clusters.stream().map(e-> {
                try {
                    return (Cluster)e.clone();
                } catch (CloneNotSupportedException ex) {
                    throw new RuntimeException(ex);
                }
            }).collect(Collectors.toList());
            clusters = assignPointsToClusters(points, clusters);
            log.info("On iteration " + i + " clusters " + clusters.toString());
            clusters = adjustClusterCenters(clusters);
            log.info("On iteration " + i + " clusters " + clusters.toString());
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }
        log.info("On iteration " + i + " result clusters: " + clusters.toString());
        return clusters;
    }

    // support methods for clustering

    /**
     * Calculates the approximate distance between two geographic points on the earth. The earth is not a globe / sphere;
     * it is a very rough, slightly amorphous ellipsoid. We use the haversine formula in order to calculate an approximate distance
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */

    /**
     * Used to generate some random initial {@link Cluster}s
     *
     * @param points
     * @param k
     * @return
     */
    protected static List<Cluster> buildRandomInitialClusters(List<LocationCoordinates> points, int k) {
        // shuffle the points randomly, then use the first k as clusters
        Collections.shuffle( points);
        List<Cluster> clusters = new ArrayList<>();
        for(int i = 0 ; i<k; i++) {
            clusters.add(new Cluster( points.get(i).getLongitude(), points.get(i).getLatitude()));
        }
        // reshuffle to avoid putting the points into the cluster they generated?
        Collections.shuffle(points);
        int i = 0;
        // distribute each random point to a cluster
        for (LocationCoordinates point : points) {
            clusters.get(i).getLocationCoordinatesSet().add(point);
            i++;
            if (i == k) {
                i = 0;
            }
        }
        return clusters;
    }

    /**
     * Distributes each point to the most appropriate {@link Cluster}, in this case, the geographic closest according to
     * the Haversine distance
     *
     * @param points
     * @param clusters
     * @return
     */
    private static List<Cluster> assignPointsToClusters(List<LocationCoordinates> points, List<Cluster> clusters) {
        // for each point, find the cluster with the closest center
        clusters.forEach(cluster -> cluster.clearPoints());

        for (LocationCoordinates point: points) {
            Cluster current = clusters.get(0);
            for (Cluster cluster: clusters) {
                if (haversineDistance(point, cluster) < haversineDistance(point, current)) {
                    current = cluster;
                }
            }
            log.debug("Adding"+point+" to "+current);
            current.getLocationCoordinatesSet().add(point);
        }
        return clusters;
    }

    /**
     * Re-adjusts the center of each {@link Cluster} based on the current {@link LocationCoordinates}s assigned to it
     *
     * @param clusters
     */
    private static List<Cluster> adjustClusterCenters(List<Cluster> clusters) {
        // calculate the mean of all lats and longs
        for (Cluster cluster : clusters) {
            if (cluster.getLocationCoordinatesSet().size() > 0) {
                cluster.setLatitude( cluster.getLocationCoordinatesSet().stream().map(e -> e.getLatitude()).collect(Collectors.summingDouble(Double::doubleValue)) / cluster.getLocationCoordinatesSet().size());
                cluster.setLongitude( cluster.getLocationCoordinatesSet().stream().map(e -> e.getLongitude()).collect(Collectors.summingDouble(Double::doubleValue)) / cluster.getLocationCoordinatesSet().size());
            }
        }
        return clusters;
    }

    /**
     * Determines if the {@link Cluster}s have converged. This is done by comparing the contents of the current cluster
     * iteration and the previous to see if any {@link LocationCoordinates}s have moved Clusters. If no movement has occured,
     * we assume the clusters are stable
     *
     * @param previous
     * @param current
     * @return
     */
    private static boolean hasConverged(List<Cluster> previous, List<Cluster> current) {
        if (previous.size() != current.size()) {
            throw new IllegalArgumentException("Clusters must be the same size");

        }
        for (int i = 0; i < previous.size(); i++) {
            if (!previous.get(i).getLocationCoordinatesSet().equals(current.get(i).getLocationCoordinatesSet())) {
                return false;
            }
        }
        log.debug("Converged!");
        return true;
    }
}
