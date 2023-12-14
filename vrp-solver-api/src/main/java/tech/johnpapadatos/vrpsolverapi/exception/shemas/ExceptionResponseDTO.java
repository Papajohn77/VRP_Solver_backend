package tech.johnpapadatos.vrpsolverapi.exception.shemas;

import java.time.ZonedDateTime;

public record ExceptionResponseDTO(
    String detail, 
    ZonedDateTime timestamp
) {}
