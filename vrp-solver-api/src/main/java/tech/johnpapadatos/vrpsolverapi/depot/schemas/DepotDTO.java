package tech.johnpapadatos.vrpsolverapi.depot.schemas;

public record DepotDTO(
    Integer id,
    String name,
    double latitude,
    double longitude,
    String address
) {}
