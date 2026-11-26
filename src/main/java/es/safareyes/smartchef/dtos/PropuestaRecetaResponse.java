package es.safareyes.smartchef.dtos;

import java.util.List;

public record PropuestaRecetaResponse(
        RecetaResponse receta,
        int ingredientesTotales,
        int ingredientesDisponibles,
        int porcentajeCoincidencia,
        List<IngredienteResponse> faltantes
) {}
