package tech.johnpapadatos.vrpsolverapi.solver.googlemaps;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.google.maps.model.LatLng;

@Service
@ConditionalOnProperty(
    value = "google.maps.api.enabled",
    havingValue = "false"
)
public class MockGoogleMapsService implements DistanceMatrixGenerator {
    
    @Override
    public int[][] createDistanceMatrix(List<LatLng> locations) {
        return new int[][] {
            {0, 752, 593, 903},
            {911, 0, 1505, 1225},
            {800, 556, 0, 1306},
            {1456, 975, 1375, 0}
        };
    }
}
