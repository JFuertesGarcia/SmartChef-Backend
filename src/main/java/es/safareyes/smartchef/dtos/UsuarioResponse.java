package es.safareyes.smartchef.dtos;

import java.util.List;

public record UsuarioResponse(
        Integer id,
        Integer rol,
        String nombre,
        List<PreferenciaResponse> preferencias,
        List<IngredienteResponse> listaCompra
) {}
