package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.RecetaIngredienteRequest;
import es.safareyes.smartchef.dtos.RecetaIngredienteResponse;
import es.safareyes.smartchef.modelos.Ingrediente;
import es.safareyes.smartchef.modelos.Receta;
import es.safareyes.smartchef.modelos.RecetaIngrediente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecetaIngredienteMapper {

    private final IngredienteMapper ingredienteMapper;

    /** Receta e ingrediente se buscan antes en el servicio con sus ids. */
    public RecetaIngrediente toEntity(RecetaIngredienteRequest request, Receta receta, Ingrediente ingrediente) {
        RecetaIngrediente recetaIngrediente = new RecetaIngrediente();
        updateEntity(recetaIngrediente, request, receta, ingrediente);
        return recetaIngrediente;
    }

    public void updateEntity(RecetaIngrediente recetaIngrediente, RecetaIngredienteRequest request,
                             Receta receta, Ingrediente ingrediente) {
        recetaIngrediente.setReceta(receta);
        recetaIngrediente.setIngrediente(ingrediente);
        recetaIngrediente.setCantidad(request.cantidad());
    }

    public RecetaIngredienteResponse toResponse(RecetaIngrediente recetaIngrediente) {
        return new RecetaIngredienteResponse(
                recetaIngrediente.getId(),
                recetaIngrediente.getReceta() != null ? recetaIngrediente.getReceta().getId() : null,
                ingredienteMapper.toResponse(recetaIngrediente.getIngrediente()),
                recetaIngrediente.getCantidad()
        );
    }
}
