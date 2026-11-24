package es.safareyes.smartchef.dtos;

public record RecetaResponse(
        Integer id,
        String nombre,
        String descripcion,
        Integer raciones,
        Float tiempo,
        Integer calorias,
        Integer dificultad
) {}
