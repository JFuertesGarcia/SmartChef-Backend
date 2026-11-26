package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.IngredienteRequest;
import es.safareyes.smartchef.dtos.IngredienteResponse;
import es.safareyes.smartchef.modelos.Ingrediente;
import org.springframework.stereotype.Component;

@Component
public class IngredienteMapper {

    public Ingrediente toEntity(IngredienteRequest request) {
        Ingrediente ingrediente = new Ingrediente();
        updateEntity(ingrediente, request);
        return ingrediente;
    }

    public void updateEntity(Ingrediente ingrediente, IngredienteRequest request) {
        ingrediente.setNombre(request.nombre());
        ingrediente.setVegetariano(request.vegetariano());
        ingrediente.setSinGluten(request.sinGluten());
        ingrediente.setCalorias(request.calorias());
        ingrediente.setTipoCantidad(request.tipoCantidad());
        ingrediente.setPrecio(request.precio());
    }

    public IngredienteResponse toResponse(Ingrediente ingrediente) {
        return new IngredienteResponse(
                ingrediente.getId(),
                ingrediente.getNombre(),
                ingrediente.getVegetariano(),
                ingrediente.getSinGluten(),
                ingrediente.getCalorias(),
                ingrediente.getTipoCantidad(),
                ingrediente.getPrecio()
        );
    }
}
