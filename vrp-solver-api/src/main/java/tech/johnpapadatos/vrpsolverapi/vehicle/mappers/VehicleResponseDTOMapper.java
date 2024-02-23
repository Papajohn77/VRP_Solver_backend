package tech.johnpapadatos.vrpsolverapi.vehicle.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import tech.johnpapadatos.vrpsolverapi.vehicle.Vehicle;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleResponseDTO;

@Component
public class VehicleResponseDTOMapper implements Function<Vehicle, VehicleResponseDTO> {
    @Override
    public VehicleResponseDTO apply(Vehicle vehicle) {
        return new VehicleResponseDTO(
            vehicle.getId(), 
            vehicle.getName(), 
            vehicle.getCapacity()
        );
    }
}
