package tech.johnpapadatos.vrpsolverapi.customer.schemas;

public record CustomerResponseDTO(
    Integer id,
    String name,
    int demand,
    double latitude,
    double longitude,
    String address
) {}
