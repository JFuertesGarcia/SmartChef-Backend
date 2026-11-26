package es.safareyes.smartchef.servicios;

import es.safareyes.smartchef.constantes.Roles;
import es.safareyes.smartchef.constantes.TipoRegistro;
import es.safareyes.smartchef.dtos.InformeRecetasPopularesResponse;
import es.safareyes.smartchef.dtos.RecetaPopularResponse;
import es.safareyes.smartchef.excepciones.ReglaNegocioException;
import es.safareyes.smartchef.mappers.RecetaMapper;
import es.safareyes.smartchef.modelos.Receta;
import es.safareyes.smartchef.modelos.Usuario;
import es.safareyes.smartchef.repositorios.RecetaConteo;
import es.safareyes.smartchef.repositorios.RecetaRepository;
import es.safareyes.smartchef.repositorios.RecetaUsuarioRepository;
import es.safareyes.smartchef.seguridad.ControlAcceso;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InformeService {

    private static final int TOP = 10;
    private static final int DIAS_POR_DEFECTO = 30;

    private final RecetaUsuarioRepository recetaUsuarioRepository;
    private final RecetaRepository recetaRepository;
    private final RecetaMapper recetaMapper;
    private final ControlAcceso controlAcceso;

    /** GET /informes/recetas-populares?desde=&hasta= (ADMIN, CHEF). Ambas fechas incluidas. */
    @Transactional(readOnly = true)
    public InformeRecetasPopularesResponse recetasPopulares(Integer usuarioId, LocalDate desde, LocalDate hasta) {
        Usuario usuario = controlAcceso.usuario(usuarioId);
        controlAcceso.exigirRol(usuario, Roles.ADMIN, Roles.CHEF);

        LocalDate fin = hasta != null ? hasta : LocalDate.now();
        LocalDate inicio = desde != null ? desde : fin.minusDays(DIAS_POR_DEFECTO);
        if (inicio.isAfter(fin)) {
            throw new ReglaNegocioException("'desde' no puede ser posterior a 'hasta'");
        }
        LocalDateTime limiteInferior = inicio.atStartOfDay();
        LocalDateTime limiteSuperior = fin.plusDays(1).atStartOfDay();

        return new InformeRecetasPopularesResponse(inicio, fin,
                top(TipoRegistro.COCINADA, limiteInferior, limiteSuperior),
                top(TipoRegistro.GUARDADA, limiteInferior, limiteSuperior));
    }

    private List<RecetaPopularResponse> top(int tipo, LocalDateTime desde, LocalDateTime hasta) {
        List<RecetaConteo> conteos = recetaUsuarioRepository.contarPorReceta(
                tipo, desde, hasta, PageRequest.of(0, TOP));
        Map<Integer, Receta> recetas = recetaRepository
                .findAllById(conteos.stream().map(RecetaConteo::getRecetaId).toList()).stream()
                .collect(Collectors.toMap(Receta::getId, Function.identity()));
        return conteos.stream()
                .filter(c -> recetas.containsKey(c.getRecetaId()))
                .map(c -> new RecetaPopularResponse(recetaMapper.toResponse(recetas.get(c.getRecetaId())), c.getTotal()))
                .toList();
    }
}
