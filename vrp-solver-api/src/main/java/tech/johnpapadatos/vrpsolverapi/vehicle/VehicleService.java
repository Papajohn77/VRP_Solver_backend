package tech.johnpapadatos.vrpsolverapi.vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import tech.johnpapadatos.vrpsolverapi.exception.AlreadyExistsException;
import tech.johnpapadatos.vrpsolverapi.exception.NotFoundException;
import tech.johnpapadatos.vrpsolverapi.model.Model;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleResponseDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehiclesResponseDTO;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final ModelRepository modelRepository;

    public VehicleService(
        VehicleRepository vehicleRepository,
        ModelRepository modelRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.modelRepository = modelRepository;
    }

    public VehiclesResponseDTO getVehiclesByModelId(int modelId) {
        Optional<Model> model = modelRepository.findById(modelId);
        if (!model.isPresent()) {
            throw new NotFoundException(
                "There is no model with id=" + modelId
            );
        }

        List<Vehicle> vehicles = model.get().getVehicles();
        return convertVehiclesToVehiclesResponseDTO(vehicles);
    }

    public VehicleCreateResponseDTO createVehicle(
        VehicleCreateRequestDTO vehicleToCreate
    ) {
        Optional<Model> model = modelRepository.findById(
            vehicleToCreate.modelId()
        );
        if (!model.isPresent()) {
            throw new NotFoundException(
                "There is no model with id=" + vehicleToCreate.modelId()
            );
        }

        Optional<Vehicle> vehicle = vehicleRepository.findByNameAndModelId(
            vehicleToCreate.name(), vehicleToCreate.modelId()
        );
        if (vehicle.isPresent()) {
            throw new AlreadyExistsException(
                "Failed to create vehicle! There is already a vehicle with that name in the selected model."
            );
        }

        Vehicle savedVehicle = vehicleRepository.save(
            Vehicle.builder()
                .name(vehicleToCreate.name())
                .capacity(vehicleToCreate.capacity())
                .model(model.get())
                .build()
        );
        return new VehicleCreateResponseDTO(savedVehicle.getId());
    }

    public void deleteVehicle(int id) {
        // Could check if exists
        vehicleRepository.deleteById(id);
    }

    private VehiclesResponseDTO convertVehiclesToVehiclesResponseDTO(
        List<Vehicle> vehicles
    ) {
        List<VehicleResponseDTO> vehicleResponses = new ArrayList<>();
        for (var vehicle : vehicles) {
            vehicleResponses.add(
                new VehicleResponseDTO(
                    vehicle.getId(), 
                    vehicle.getName(), 
                    vehicle.getCapacity()
                )
            );
        }
        return new VehiclesResponseDTO(vehicleResponses);
    }
}
