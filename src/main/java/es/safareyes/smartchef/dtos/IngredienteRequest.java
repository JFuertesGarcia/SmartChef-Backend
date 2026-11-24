package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.*;

public record IngredienteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        Boolean vegetariano,
        Boolean sinGluten,

        @PositiveOrZero(message = "Las calorías no pueden ser negativas")
        Integer calorias,

        Integer tipoCantidad,

        @PositiveOrZero(message = "El precio no puede ser negativo")
        Float precio
) {}
