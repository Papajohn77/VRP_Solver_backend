package tech.johnpapadatos.vrpsolverapi.vehicle;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehiclesResponseDTO;

@Tag(name = "Vehicles")
@RestController
@RequestMapping("vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public VehiclesResponseDTO getVehiclesByModelId(
        @RequestParam("model_id") int modelId
    ) {
        return vehicleService.getVehiclesByModelId(modelId);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public VehicleCreateResponseDTO createVehicle(
        @Valid @RequestBody VehicleCreateRequestDTO vehicleToCreate
    ) {
        return vehicleService.createVehicle(vehicleToCreate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteVehicle(@PathVariable int id) {
        vehicleService.deleteVehicle(id);
    }
}
