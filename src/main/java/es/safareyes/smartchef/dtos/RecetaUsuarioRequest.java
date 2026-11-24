package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.NotNull;

public record RecetaUsuarioRequest(
        @NotNull(message = "La receta es obligatoria")
        Integer idReceta,

        @NotNull(message = "El usuario es obligatorio")
        Integer idUsuario,

        @NotNull(message = "El tipo es obligatorio")
        Integer tipo
) {}
