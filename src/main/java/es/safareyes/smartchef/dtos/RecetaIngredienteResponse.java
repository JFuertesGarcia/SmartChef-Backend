package es.safareyes.smartchef.dtos;

public record RecetaIngredienteResponse(
        Integer id,
        Integer idReceta,
        IngredienteResponse ingrediente,
        Integer cantidad
) {}
