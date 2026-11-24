package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.*;

public record FotoRequest(
        @NotBlank(message = "La URL es obligatoria")
        @Size(max = 500, message = "La URL no puede superar los 500 caracteres")
        String url,

        @NotNull(message = "La receta es obligatoria")
        Integer idReceta
) {}
