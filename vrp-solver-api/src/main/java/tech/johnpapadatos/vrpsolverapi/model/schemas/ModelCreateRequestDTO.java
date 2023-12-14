package tech.johnpapadatos.vrpsolverapi.model.schemas;

import jakarta.validation.constraints.NotBlank;

public record ModelCreateRequestDTO(
    @NotBlank(message = "Model's name cannot be blank.")
    String name
) {}
