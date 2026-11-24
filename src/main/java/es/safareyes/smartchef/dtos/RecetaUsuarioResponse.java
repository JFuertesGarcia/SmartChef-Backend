package es.safareyes.smartchef.dtos;

import java.time.LocalDateTime;

public record RecetaUsuarioResponse(
        Integer id,
        Integer idReceta,
        Integer idUsuario,
        Integer tipo,
        LocalDateTime fecha
) {}
