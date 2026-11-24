package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.RecetaRequest;
import es.safareyes.smartchef.dtos.RecetaResponse;
import es.safareyes.smartchef.modelos.Receta;
import org.springframework.stereotype.Component;

@Component
public class RecetaMapper {

    public Receta toEntity(RecetaRequest request) {
        Receta receta = new Receta();
        updateEntity(receta, request);
        return receta;
    }

    public void updateEntity(Receta receta, RecetaRequest request) {
        receta.setNombre(request.nombre());
        receta.setDescripcion(request.descripcion());
        receta.setRaciones(request.raciones());
        receta.setTiempo(request.tiempo());
        receta.setCalorias(request.calorias());
        receta.setDificultad(request.dificultad());
    }

    public RecetaResponse toResponse(Receta receta) {
        return new RecetaResponse(
                receta.getId(),
                receta.getNombre(),
                receta.getDescripcion(),
                receta.getRaciones(),
                receta.getTiempo(),
                receta.getCalorias(),
                receta.getDificultad()
        );
    }
}
