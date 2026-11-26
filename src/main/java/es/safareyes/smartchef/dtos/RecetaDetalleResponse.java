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
        Double coste,
        Boolean vegetariana,
        Boolean sinGluten,
        List<PasoResponse> pasos,
        List<RecetaIngredienteResponse> ingredientes,
        List<FotoResponse> fotos
) {}
