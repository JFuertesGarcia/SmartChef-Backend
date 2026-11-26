package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.IngredienteResponse;
import es.safareyes.smartchef.dtos.PreferenciaResponse;
import es.safareyes.smartchef.dtos.UsuarioRequest;
import es.safareyes.smartchef.dtos.UsuarioResponse;
import es.safareyes.smartchef.modelos.Preferencia;
import es.safareyes.smartchef.modelos.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final PreferenciaMapper preferenciaMapper;
    private final IngredienteMapper ingredienteMapper;

    /**
     * La contraseña llega ya cifrada (PasswordEncoder en el servicio) y las
     * preferencias ya buscadas con preferenciasIds. El rol se asigna en el servicio.
     */
    public Usuario toEntity(UsuarioRequest request, String contrasenyaCifrada, Set<Preferencia> preferencias) {
        Usuario usuario = new Usuario();
        updateEntity(usuario, request, contrasenyaCifrada, preferencias);
        return usuario;
    }

    public void updateEntity(Usuario usuario, UsuarioRequest request,
                             String contrasenyaCifrada, Set<Preferencia> preferencias) {
        usuario.setNombre(request.nombre());
        usuario.setContrasenya(contrasenyaCifrada);
        usuario.getPreferencias().clear();
        if (preferencias != null) {
            usuario.getPreferencias().addAll(preferencias);
        }
    }

    /** Recorre relaciones LAZY (preferencias y listaCompra): llámalo dentro de una transacción. */
    public UsuarioResponse toResponse(Usuario usuario) {
        List<PreferenciaResponse> preferencias = usuario.getPreferencias().stream()
                .map(preferenciaMapper::toResponse)
                .toList();
        List<IngredienteResponse> listaCompra = usuario.getListaCompra().stream()
                .map(ingredienteMapper::toResponse)
                .toList();
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getRol(),
                usuario.getNombre(),
                preferencias,
                listaCompra
        );
    }
}
