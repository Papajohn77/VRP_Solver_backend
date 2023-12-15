package tech.johnpapadatos.vrpsolverapi.solver.googlemaps;

import java.util.List;

import com.google.maps.model.LatLng;

public interface DistanceMatrixGenerator {
    int[][] createDistanceMatrix(List<LatLng> locations);
}
