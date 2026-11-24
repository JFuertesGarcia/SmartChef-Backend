package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.*;

public record RecetaIngredienteRequest(
        @NotNull(message = "La receta es obligatoria")
        Integer idReceta,

        @NotNull(message = "El ingrediente es obligatorio")
        Integer idIngrediente,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que 0")
        Integer cantidad
) {}
