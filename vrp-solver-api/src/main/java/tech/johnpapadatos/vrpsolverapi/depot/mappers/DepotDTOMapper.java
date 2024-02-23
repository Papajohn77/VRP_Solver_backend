package tech.johnpapadatos.vrpsolverapi.depot.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import tech.johnpapadatos.vrpsolverapi.depot.Depot;
import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotDTO;

@Component
public class DepotDTOMapper implements Function<Depot, DepotDTO> {
    @Override
    public DepotDTO apply(Depot depot) {
        return new DepotDTO(
            depot.getId(), 
            depot.getName(), 
            depot.getLatitude(), 
            depot.getLongitude(), 
            depot.getAddress()
        );
    }
}
