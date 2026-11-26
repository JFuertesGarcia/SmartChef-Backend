package es.safareyes.smartchef.seguridad;

import es.safareyes.smartchef.excepciones.AccesoDenegadoException;
import es.safareyes.smartchef.excepciones.NoAutenticadoException;
import es.safareyes.smartchef.modelos.Usuario;
import es.safareyes.smartchef.repositorios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Carga el usuario que hace la petición y comprueba roles.
 * Cuando añadas Spring Security, solo cambia de dónde sale el id (token en lugar de cabecera).
 */
@Component
@RequiredArgsConstructor
public class ControlAcceso {

    private final UsuarioRepository usuarioRepository;

    public Usuario usuario(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoAutenticadoException("Usuario no identificado"));
    }

    public boolean tieneRol(Usuario usuario, int... roles) {
        if (usuario.getRol() == null) {
            return false;
        }
        for (int rol : roles) {
            if (usuario.getRol() == rol) {
                return true;
            }
        }
        return false;
    }

    public void exigirRol(Usuario usuario, int... roles) {
        if (!tieneRol(usuario, roles)) {
            throw new AccesoDenegadoException("No tienes permisos para esta operación");
        }
    }
}
