package es.safareyes.smartchef.servicios;

import es.safareyes.smartchef.dtos.BusquedaPorIngredientesRequest;
import es.safareyes.smartchef.dtos.IngredienteResponse;
import es.safareyes.smartchef.dtos.PropuestaRecetaResponse;
import es.safareyes.smartchef.excepciones.ReglaNegocioException;
import es.safareyes.smartchef.mappers.IngredienteMapper;
import es.safareyes.smartchef.mappers.RecetaMapper;
import es.safareyes.smartchef.modelos.RecetaIngrediente;
import es.safareyes.smartchef.repositorios.DespensaRepository;
import es.safareyes.smartchef.repositorios.RecetaIngredienteRepository;
import es.safareyes.smartchef.seguridad.ControlAcceso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** POST /recetas/buscar-por-ingredientes (RN-04). */
@Service
@RequiredArgsConstructor
public class PropuestaRecetaService {

    private static final int MAXIMO_PROPUESTAS = 20;

    private final RecetaIngredienteRepository recetaIngredienteRepository;
    private final DespensaRepository despensaRepository;
    private final RecetaMapper recetaMapper;
    private final IngredienteMapper ingredienteMapper;
    private final ControlAcceso controlAcceso;

    @Transactional(readOnly = true)
    public List<PropuestaRecetaResponse> proponer(Integer usuarioId, BusquedaPorIngredientesRequest request) {
        controlAcceso.usuario(usuarioId);

        Set<Integer> disponibles;
        if (request != null && request.ingredientesIds() != null && !request.ingredientesIds().isEmpty()) {
            disponibles = new HashSet<>(request.ingredientesIds());
        } else {
            disponibles = new HashSet<>(despensaRepository.findIdsIngredientesVigentes(usuarioId, LocalDate.now()));
        }
        if (disponibles.isEmpty()) {
            throw new ReglaNegocioException(
                    "No hay ingredientes con los que buscar: envía una lista o llena tu despensa");
        }

        Map<Integer, List<RecetaIngrediente>> porReceta = recetaIngredienteRepository
                .findDeRecetasConAlgunIngrediente(disponibles).stream()
                .collect(Collectors.groupingBy(ri -> ri.getReceta().getId(), LinkedHashMap::new, Collectors.toList()));

        return porReceta.values().stream()
                .map(lineas -> construirPropuesta(lineas, disponibles))
                .sorted(Comparator.comparingInt(PropuestaRecetaResponse::porcentajeCoincidencia).reversed()
                        .thenComparingInt(p -> p.faltantes().size())
                        .thenComparing(p -> p.receta().nombre(), Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(MAXIMO_PROPUESTAS)
                .toList();
    }

    private PropuestaRecetaResponse construirPropuesta(List<RecetaIngrediente> lineas, Set<Integer> disponibles) {
        int total = lineas.size();
        List<IngredienteResponse> faltantes = lineas.stream()
                .filter(ri -> !disponibles.contains(ri.getIngrediente().getId()))
                .map(ri -> ingredienteMapper.toResponse(ri.getIngrediente()))
                .toList();
        int cubiertos = total - faltantes.size();
        int porcentaje = (int) Math.round(100.0 * cubiertos / total);
        return new PropuestaRecetaResponse(
                recetaMapper.toResponse(lineas.get(0).getReceta()),
                total, cubiertos, porcentaje, faltantes);
    }
}
