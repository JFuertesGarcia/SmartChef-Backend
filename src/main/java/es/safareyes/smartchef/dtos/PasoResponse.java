package es.safareyes.smartchef.dtos;

public record PasoResponse(
        Integer id,
        Integer idReceta,
        String descripcion,
        Integer numero
) {}
