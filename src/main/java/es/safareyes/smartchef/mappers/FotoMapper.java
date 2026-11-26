package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.FotoRequest;
import es.safareyes.smartchef.dtos.FotoResponse;
import es.safareyes.smartchef.modelos.Foto;
import es.safareyes.smartchef.modelos.Receta;
import org.springframework.stereotype.Component;

@Component
public class FotoMapper {

    /** La receta se busca antes en el servicio con idReceta. */
    public Foto toEntity(FotoRequest request, Receta receta) {
        Foto foto = new Foto();
        updateEntity(foto, request, receta);
        return foto;
    }

    public void updateEntity(Foto foto, FotoRequest request, Receta receta) {
        foto.setUrl(request.url());
        foto.setReceta(receta);
    }

    public FotoResponse toResponse(Foto foto) {
        return new FotoResponse(
                foto.getId(),
                foto.getUrl(),
                foto.getReceta() != null ? foto.getReceta().getId() : null
        );
    }
}
