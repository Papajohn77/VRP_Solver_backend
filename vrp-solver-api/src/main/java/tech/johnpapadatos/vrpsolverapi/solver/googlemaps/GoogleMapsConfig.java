package tech.johnpapadatos.vrpsolverapi.solver.googlemaps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.maps.GeoApiContext;

@Configuration
@ConditionalOnProperty(
    name = "google.maps.api.enabled", 
    havingValue = "true"
)
public class GoogleMapsConfig {

    @Bean
    public GeoApiContext googleMapsClient(
        @Value("${google.maps.api.key}") String apiKey
    ) {
        return new GeoApiContext.Builder()
            .apiKey(apiKey)
            .build();
    }
}
