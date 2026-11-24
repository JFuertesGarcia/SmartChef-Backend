package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.*;

public record RecetaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String nombre,

        @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
        String descripcion,

        @NotNull(message = "Las raciones son obligatorias")
        @Min(value = 1, message = "Debe haber al menos 1 ración")
        Integer raciones,

        @NotNull(message = "El tiempo es obligatorio")
        @Positive(message = "El tiempo debe ser mayor que 0")
        Float tiempo,

        @PositiveOrZero(message = "Las calorías no pueden ser negativas")
        Integer calorias,

        @NotNull(message = "La dificultad es obligatoria")
        @Min(value = 1, message = "La dificultad mínima es 1")
        @Max(value = 5, message = "La dificultad máxima es 5")
        Integer dificultad
) {}
