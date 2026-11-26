package es.safareyes.smartchef.dtos;

public record RecetaPopularResponse(
        RecetaResponse receta,
        long total
) {}
