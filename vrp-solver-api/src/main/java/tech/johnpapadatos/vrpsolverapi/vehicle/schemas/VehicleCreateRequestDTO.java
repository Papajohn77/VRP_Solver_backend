package tech.johnpapadatos.vrpsolverapi.vehicle.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VehicleCreateRequestDTO(
    @NotBlank(message = "Vehicles's name cannot be blank.")
    String name,
    @Positive(message = "Vehicles's capacity cannot be a negative number.")
    @NotNull(message = "Vehicles's capacity must be provided.")
    Integer capacity,
    @JsonProperty("model_id")
    @NotNull(message = "Vehicles's model_id must be provided.")
    Integer modelId
) {}
