package es.safareyes.smartchef.dtos;

import java.util.List;

/** Si la lista viene vacía o sin enviar, se usa la despensa del usuario. */
public record BusquedaPorIngredientesRequest(
        List<Integer> ingredientesIds
) {}
