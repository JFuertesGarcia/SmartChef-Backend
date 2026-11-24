package es.safareyes.smartchef.dtos;

public record IngredienteResponse(
        Integer id,
        String nombre,
        Boolean vegetariano,
        Boolean sinGluten,
        Integer calorias,
        Integer tipoCantidad,
        Float precio
) {}
