package es.safareyes.smartchef.dtos;

import java.time.LocalDateTime;

public record RegistroItemResponse(
        Integer id,
        LocalDateTime fecha,
        RecetaResponse receta
) {}
