package tech.johnpapadatos.vrpsolverapi.solver.schemas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import tech.johnpapadatos.vrpsolverapi.solver.pojos.RoutePointPOJO;

public record SolutionDTO(
    @JsonProperty("total_distance_meters")
    int totalDistanceMeters,
    List<List<RoutePointPOJO>> routes,
    List<String> vehicles
) {}
