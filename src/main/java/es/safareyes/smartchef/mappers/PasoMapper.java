package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.PasoRequest;
import es.safareyes.smartchef.dtos.PasoResponse;
import es.safareyes.smartchef.modelos.Paso;
import es.safareyes.smartchef.modelos.Receta;
import org.springframework.stereotype.Component;

@Component
public class PasoMapper {

    /** La receta se busca antes en el servicio con idReceta. */
    public Paso toEntity(PasoRequest request, Receta receta) {
        Paso paso = new Paso();
        updateEntity(paso, request, receta);
        return paso;
    }

    public void updateEntity(Paso paso, PasoRequest request, Receta receta) {
        paso.setReceta(receta);
        paso.setDescripcion(request.descripcion());
        paso.setNumero(request.numero());
    }

    public PasoResponse toResponse(Paso paso) {
        return new PasoResponse(
                paso.getId(),
                paso.getReceta() != null ? paso.getReceta().getId() : null,
                paso.getDescripcion(),
                paso.getNumero()
        );
    }
}
