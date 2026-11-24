package es.safareyes.smartchef.dtos;

import java.util.List;

public record RecetaDetalleResponse(
        Integer id,
        String nombre,
        String descripcion,
        Integer raciones,
        Float tiempo,
        Integer calorias,
        Integer dificultad,
        List<PasoResponse> pasos,
        List<RecetaIngredienteResponse> ingredientes,
        List<FotoResponse> fotos
) {}
