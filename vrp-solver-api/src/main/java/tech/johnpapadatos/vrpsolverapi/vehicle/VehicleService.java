package tech.johnpapadatos.vrpsolverapi.vehicle;

import java.util.Optional;

import org.springframework.stereotype.Service;

import tech.johnpapadatos.vrpsolverapi.exception.AlreadyExistsException;
import tech.johnpapadatos.vrpsolverapi.exception.NotFoundException;
import tech.johnpapadatos.vrpsolverapi.model.Model;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;
import tech.johnpapadatos.vrpsolverapi.vehicle.mappers.VehicleResponseDTOMapper;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehiclesResponseDTO;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final VehicleResponseDTOMapper vehicleResponseDTOMapper;
    private final ModelRepository modelRepository;

    public VehicleService(
        VehicleRepository vehicleRepository,
        VehicleResponseDTOMapper vehicleResponseDTOMapper,
        ModelRepository modelRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleResponseDTOMapper = vehicleResponseDTOMapper;
        this.modelRepository = modelRepository;
    }

    public VehiclesResponseDTO getVehiclesByModelId(int modelId) {
        Optional<Model> model = modelRepository.findById(modelId);
        if (!model.isPresent()) {
            throw new NotFoundException(
                "There is no model with id=" + modelId
            );
        }

        return new VehiclesResponseDTO(
            model.get()
                .getVehicles()
                .stream()
                .map(vehicleResponseDTOMapper)
                .toList()
        );
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
}
