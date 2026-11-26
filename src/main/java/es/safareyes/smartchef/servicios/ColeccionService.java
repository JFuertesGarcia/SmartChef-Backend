package es.safareyes.smartchef.servicios;

import es.safareyes.smartchef.constantes.TipoRegistro;
import es.safareyes.smartchef.dtos.ColeccionResponse;
import es.safareyes.smartchef.excepciones.RecursoNoEncontradoException;
import es.safareyes.smartchef.excepciones.ReglaNegocioException;
import es.safareyes.smartchef.mappers.ColeccionMapper;
import es.safareyes.smartchef.modelos.Coleccion;
import es.safareyes.smartchef.modelos.Receta;
import es.safareyes.smartchef.modelos.RecetaUsuario;
import es.safareyes.smartchef.modelos.Usuario;
import es.safareyes.smartchef.repositorios.ColeccionRepository;
import es.safareyes.smartchef.repositorios.RecetaRepository;
import es.safareyes.smartchef.repositorios.RecetaUsuarioRepository;
import es.safareyes.smartchef.seguridad.ControlAcceso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColeccionService {

    private static final int LONGITUD_MAXIMA_NOMBRE = 100;

    private final ColeccionRepository coleccionRepository;
    private final RecetaRepository recetaRepository;
    private final RecetaUsuarioRepository recetaUsuarioRepository;
    private final ColeccionMapper coleccionMapper;
    private final ControlAcceso controlAcceso;

    /** GET /colecciones: mis colecciones con sus recetas. */
    @Transactional(readOnly = true)
    public List<ColeccionResponse> listar(Integer usuarioId) {
        controlAcceso.usuario(usuarioId);
        return coleccionRepository.findByUsuarioConRecetas(usuarioId).stream()
                .map(coleccionMapper::toResponse)
                .toList();
    }

    /**
     * PUT /colecciones/{nombre}/recetas/{recetaId}: guarda la receta en la colección (la crea si no existe).
     * Es idempotente. La primera vez que se guarda se anota un RecetaUsuario de tipo GUARDADA
     * (con fecha) para poder sacar el informe de recetas más guardadas por periodo.
     */
    @Transactional
    public ColeccionResponse guardarReceta(Integer usuarioId, String nombre, Integer recetaId) {
        Usuario usuario = controlAcceso.usuario(usuarioId);

        String nombreLimpio = nombre == null ? "" : nombre.trim();
        if (nombreLimpio.isEmpty() || nombreLimpio.length() > LONGITUD_MAXIMA_NOMBRE) {
            throw new ReglaNegocioException(
                    "El nombre de la colección es obligatorio y no puede superar los "
                            + LONGITUD_MAXIMA_NOMBRE + " caracteres");
        }
        Receta receta = recetaRepository.findByIdAndActivaTrue(recetaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada: " + recetaId));

        Coleccion coleccion = coleccionRepository
                .findByUsuarioIdAndNombreIgnoreCase(usuarioId, nombreLimpio)
                .orElseGet(() -> {
                    Coleccion nueva = new Coleccion();
                    nueva.setNombre(nombreLimpio);
                    nueva.setUsuario(usuario);
                    return nueva;
                });

        boolean recienGuardada = coleccion.getRecetas().add(receta);
        coleccion = coleccionRepository.save(coleccion);

        if (recienGuardada) {
            RecetaUsuario registro = new RecetaUsuario();
            registro.setReceta(receta);
            registro.setUsuario(usuario);
            registro.setTipo(TipoRegistro.GUARDADA);
            registro.setFecha(LocalDateTime.now());
            recetaUsuarioRepository.save(registro);
        }
        return coleccionMapper.toResponse(coleccion);
    }
}
