package es.safareyes.smartchef.dtos;

import java.util.List;

public record ColeccionResponse(
        Integer id,
        String nombre,
        Integer idUsuario,
        List<RecetaResponse> recetas
) {}
