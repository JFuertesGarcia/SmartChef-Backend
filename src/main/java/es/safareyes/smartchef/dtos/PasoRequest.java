package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.*;

public record PasoRequest(
        @NotNull(message = "La receta es obligatoria")
        Integer idReceta,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotNull(message = "El número de paso es obligatorio")
        @Min(value = 1, message = "El número de paso empieza en 1")
        Integer numero
) {}
