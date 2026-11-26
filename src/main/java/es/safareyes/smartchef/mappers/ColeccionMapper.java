package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.ColeccionRequest;
import es.safareyes.smartchef.dtos.ColeccionResponse;
import es.safareyes.smartchef.dtos.RecetaResponse;
import es.safareyes.smartchef.modelos.Coleccion;
import es.safareyes.smartchef.modelos.Receta;
import es.safareyes.smartchef.modelos.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ColeccionMapper {

    private final RecetaMapper recetaMapper;

    /**
     * El usuario y las recetas se buscan antes en el servicio
     * con idUsuario y recetasIds (por ejemplo, recetaRepository.findAllById(...)).
     */
    public Coleccion toEntity(ColeccionRequest request, Usuario usuario, Set<Receta> recetas) {
        Coleccion coleccion = new Coleccion();
        updateEntity(coleccion, request, usuario, recetas);
        return coleccion;
    }

    public void updateEntity(Coleccion coleccion, ColeccionRequest request,
                             Usuario usuario, Set<Receta> recetas) {
        coleccion.setNombre(request.nombre());
        coleccion.setUsuario(usuario);
        coleccion.getRecetas().clear();
        if (recetas != null) {
            coleccion.getRecetas().addAll(recetas);
        }
    }

    /** Recorre la relación LAZY de recetas: llámalo dentro de una transacción. */
    public ColeccionResponse toResponse(Coleccion coleccion) {
        List<RecetaResponse> recetas = coleccion.getRecetas().stream()
                .map(recetaMapper::toResponse)
                .toList();
        return new ColeccionResponse(
                coleccion.getId(),
                coleccion.getNombre(),
                coleccion.getUsuario() != null ? coleccion.getUsuario().getId() : null,
                recetas
        );
    }
}
