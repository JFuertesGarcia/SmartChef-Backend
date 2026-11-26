package es.safareyes.smartchef.dtos;

import java.util.List;

public record ListaCompraResponse(
        List<IngredienteResponse> ingredientes,
        int anyadidos
) {}
