package tech.johnpapadatos.vrpsolverapi.solver.googlemaps;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import tech.johnpapadatos.vrpsolverapi.exception.BadGatewayException;

@Service
@ConditionalOnProperty(
    value = "google.maps.api.enabled",
    havingValue = "true"
)
public class GoogleMapsService implements DistanceMatrixGenerator {
    private final GeoApiContext googleMapsClient;

    public GoogleMapsService(GeoApiContext googleMapsClient) {
        this.googleMapsClient = googleMapsClient;
    }

    @Override
    public int[][] createDistanceMatrix(List<LatLng> locations) {
        int n = locations.size();
        int[][] distanceMatrix = new int[n][n];

        DistanceMatrix distanceMatrixModel;
        try {
            distanceMatrixModel = DistanceMatrixApi
                .newRequest(googleMapsClient)
                .origins(locations.toArray(new LatLng[0]))
                .destinations(locations.toArray(new LatLng[0]))
                .mode(TravelMode.DRIVING)
                .await();
        } catch (ApiException | InterruptedException | IOException e) {
            throw new BadGatewayException(
                "There was an issue with Google Maps. Please try again later..."
            );
        }

        long distance;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distance = distanceMatrixModel.rows[i].elements[j].distance.inMeters;
                distanceMatrix[i][j] = (int) distance;
            }
        }

        return distanceMatrix;
    }
}
