package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.RecetaUsuarioRequest;
import es.safareyes.smartchef.dtos.RecetaUsuarioResponse;
import es.safareyes.smartchef.modelos.Receta;
import es.safareyes.smartchef.modelos.RecetaUsuario;
import es.safareyes.smartchef.modelos.Usuario;
import org.springframework.stereotype.Component;

@Component
public class RecetaUsuarioMapper {

    /**
     * Receta y usuario se buscan antes en el servicio con sus ids.
     * La fecha NO se rellena aquí: ponla en el servicio con LocalDateTime.now().
     */
    public RecetaUsuario toEntity(RecetaUsuarioRequest request, Receta receta, Usuario usuario) {
        RecetaUsuario recetaUsuario = new RecetaUsuario();
        updateEntity(recetaUsuario, request, receta, usuario);
        return recetaUsuario;
    }

    public void updateEntity(RecetaUsuario recetaUsuario, RecetaUsuarioRequest request,
                             Receta receta, Usuario usuario) {
        recetaUsuario.setReceta(receta);
        recetaUsuario.setUsuario(usuario);
        recetaUsuario.setTipo(request.tipo());
    }

    public RecetaUsuarioResponse toResponse(RecetaUsuario recetaUsuario) {
        return new RecetaUsuarioResponse(
                recetaUsuario.getId(),
                recetaUsuario.getReceta() != null ? recetaUsuario.getReceta().getId() : null,
                recetaUsuario.getUsuario() != null ? recetaUsuario.getUsuario().getId() : null,
                recetaUsuario.getTipo(),
                recetaUsuario.getFecha()
        );
    }
}
