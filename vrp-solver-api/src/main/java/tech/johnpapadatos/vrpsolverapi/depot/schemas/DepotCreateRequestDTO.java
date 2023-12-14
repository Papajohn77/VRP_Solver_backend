package tech.johnpapadatos.vrpsolverapi.depot.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepotCreateRequestDTO(
    @NotBlank(message = "Depot's name cannot be blank.")
    String name,
    @NotNull(message = "Depot's latitude must be provided.")
    Double latitude,
    @NotNull(message = "Depot's longitude must be provided.")
    Double longitude,
    @NotBlank(message = "Depot's address cannot be blank.")
    String address,
    @JsonProperty("model_id")
    @NotNull(message = "Depot's model_id must be provided.")
    Integer modelId
) {}
