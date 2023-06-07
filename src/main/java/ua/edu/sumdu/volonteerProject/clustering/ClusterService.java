package ua.edu.sumdu.volonteerProject.clustering;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import ua.edu.sumdu.volonteerProject.model.LocationCoordinates;
import ua.edu.sumdu.volonteerProject.utils.CoordinateUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.sqrt;
import static ua.edu.sumdu.volonteerProject.utils.CoordinateUtils.haversineDistance;

@Slf4j
public class ClusterService {
    //taken and adopted from https://github.com/spember/geo-cluster/blob/master/src/main/groovy/pember/kmeans/geo/ClusterService.groovy.

    // radius of the earth (approx), in meters
    public static final double R = 6372.8 * 1000;

    public static List<Cluster> cluster(List<LocationCoordinates> points, int k) {
        return cluster(points, k, buildRandomInitialClustersPlusPlusEnhancement(points, k));
    }

    public static List<Cluster> cluster(List<LocationCoordinates> points, int k, List<Cluster> clusters) {
        // adapted from http://datasciencelab.wordpress.com/2013/12/12/clustering-with-k-means-in-python/
        List<Cluster> oldClusters = new ArrayList<>();
        for (Cluster e:
             clusters) {
            oldClusters.add(new Cluster(-180, -90));
        }
        log.info("clusters" + clusters.toString());
        log.info("old clusters " + oldClusters.toString());
        int i = 0;
        while (!hasConverged(oldClusters, clusters)) {
            i++;
            oldClusters = clusters.stream().map(e-> {
                try {
                    return (Cluster)e.clone();
                } catch (CloneNotSupportedException ex) {
                    throw new RuntimeException(ex);
                }
            }).collect(Collectors.toList());
            clusters = assignPointsToClusters(points, clusters);
            clusters = adjustClusterCenters(clusters);
        }
        log.info("On iteration " + i + " result clusters: " + clusters.toString());
        return clusters;
    }

    //kmeans++ modification
    protected static List<Cluster> buildRandomInitialClustersPlusPlusEnhancement(List<LocationCoordinates> points, int k){
        if(points == null || points.size() == 0){
            throw new NullPointerException();
        }
        Collections.shuffle( points);
        List<Cluster> clusters = new ArrayList<>();
        clusters.add(new Cluster( points.get(0).getLongitude(), points.get(0).getLatitude()));
        List<Double> d2;
        Random d2Random = new Random();
        while (clusters.size() < k){
              d2 = points.stream().map(e-> clusters.stream().map(cluster -> {
                double dst = CoordinateUtils.haversineDistance(e,cluster);
                return dst * dst;} )
                     .min(Double::compareTo).
                     get()).
                     collect(Collectors.toList());
              double sum =(Double) d2.stream().mapToDouble(Double::doubleValue).sum();
            List<Double> d2Probs = d2.stream().map(e->e / sum).collect(Collectors.toList());
            List<Double> d2CumProbs = new ArrayList();
            d2CumProbs.add(d2Probs.get(0));
            for (int i = 1; i<d2Probs.size(); i++) {
                d2CumProbs.add(d2Probs.get(i)+d2CumProbs.get(d2CumProbs.size()-1));
            }

            double dval = d2Random.nextDouble();
            int index = IntStream.range(0,d2CumProbs.size()).filter(i -> dval<=d2CumProbs.get(i).doubleValue()).findFirst().getAsInt();
            clusters.add(new Cluster(points.get(index).getLongitude(), points.get(index).getLatitude()));
        }
        Collections.shuffle( points);
        //random.nextInt(points.size());
        int i = 0;
        for (LocationCoordinates point : points) {
            clusters.get(i).getLocationCoordinatesSet().add(point);
            i++;
            if (i == k) {
                i = 0;
            }
        }
        return clusters;
    }

    protected static List<Cluster> buildRandomInitialClusters(List<LocationCoordinates> points, int k) {
        Collections.shuffle( points);
        List<Cluster> clusters = new ArrayList<>();
        for(int i = 0 ; i<k; i++) {
            clusters.add(new Cluster( points.get(i).getLongitude(), points.get(i).getLatitude()));
        }
        Collections.shuffle(points);
        int i = 0;
        for (LocationCoordinates point : points) {
            clusters.get(i).getLocationCoordinatesSet().add(point);
            i++;
            if (i == k) {
                i = 0;
            }
        }
        return clusters;
    }

    private static List<Cluster> assignPointsToClusters(List<LocationCoordinates> points, List<Cluster> clusters) {
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

    private static List<Cluster> adjustClusterCenters(List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            if (cluster.getLocationCoordinatesSet().size() > 0) {
                cluster.setLatitude( cluster.getLocationCoordinatesSet().stream().map(e -> e.getLatitude()).collect(Collectors.summingDouble(Double::doubleValue)) / cluster.getLocationCoordinatesSet().size());
                cluster.setLongitude( cluster.getLocationCoordinatesSet().stream().map(e -> e.getLongitude()).collect(Collectors.summingDouble(Double::doubleValue)) / cluster.getLocationCoordinatesSet().size());
            }
        }
        return clusters;
    }

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
