package tech.johnpapadatos.vrpsolverapi.customer.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CustomerCreateRequestDTO(
    @NotBlank(message = "Customer's name cannot be blank.")
    String name,
    @Positive(message = "Customer's demand cannot be a negative number.")
    @NotNull(message = "Customer's demand must be provided.")
    Integer demand,
    @NotNull(message = "Customer's latitude must be provided.")
    Double latitude,
    @NotNull(message = "Customer's longitude must be provided.")
    Double longitude,
    @NotBlank(message = "Customer's address cannot be blank.")
    String address,
    @JsonProperty("model_id")
    @NotNull(message = "Customer's model_id must be provided.")
    Integer modelId
) {}
