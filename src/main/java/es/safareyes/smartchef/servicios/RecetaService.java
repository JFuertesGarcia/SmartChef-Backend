package es.safareyes.smartchef.servicios;

import es.safareyes.smartchef.constantes.Roles;
import es.safareyes.smartchef.dtos.FotoRequest;
import es.safareyes.smartchef.dtos.IngredienteItemRequest;
import es.safareyes.smartchef.dtos.PaginaResponse;
import es.safareyes.smartchef.dtos.PasoItemRequest;
import es.safareyes.smartchef.dtos.PasoRequest;
import es.safareyes.smartchef.dtos.RecetaCompletaRequest;
import es.safareyes.smartchef.dtos.RecetaDetalleResponse;
import es.safareyes.smartchef.dtos.RecetaIngredienteRequest;
import es.safareyes.smartchef.dtos.RecetaResponse;
import es.safareyes.smartchef.excepciones.AccesoDenegadoException;
import es.safareyes.smartchef.excepciones.RecursoNoEncontradoException;
import es.safareyes.smartchef.excepciones.ReglaNegocioException;
import es.safareyes.smartchef.mappers.FotoMapper;
import es.safareyes.smartchef.mappers.PasoMapper;
import es.safareyes.smartchef.mappers.RecetaIngredienteMapper;
import es.safareyes.smartchef.mappers.RecetaMapper;
import es.safareyes.smartchef.modelos.Foto;
import es.safareyes.smartchef.modelos.Ingrediente;
import es.safareyes.smartchef.modelos.Paso;
import es.safareyes.smartchef.modelos.Receta;
import es.safareyes.smartchef.modelos.RecetaIngrediente;
import es.safareyes.smartchef.modelos.Usuario;
import es.safareyes.smartchef.repositorios.FotoRepository;
import es.safareyes.smartchef.repositorios.IngredienteRepository;
import es.safareyes.smartchef.repositorios.PasoRepository;
import es.safareyes.smartchef.repositorios.RecetaIngredienteRepository;
import es.safareyes.smartchef.repositorios.RecetaRepository;
import es.safareyes.smartchef.repositorios.RecetaSpecs;
import es.safareyes.smartchef.seguridad.ControlAcceso;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecetaService {

    private static final int TAMANYO_MAXIMO_PAGINA = 100;

    private final RecetaRepository recetaRepository;
    private final PasoRepository pasoRepository;
    private final FotoRepository fotoRepository;
    private final RecetaIngredienteRepository recetaIngredienteRepository;
    private final IngredienteRepository ingredienteRepository;
    private final RecetaMapper recetaMapper;
    private final PasoMapper pasoMapper;
    private final FotoMapper fotoMapper;
    private final RecetaIngredienteMapper recetaIngredienteMapper;
    private final ControlAcceso controlAcceso;

    /** GET /recetas: catálogo paginado con filtros (RF-02). */
    @Transactional(readOnly = true)
    public PaginaResponse<RecetaResponse> listar(String texto, Integer dificultadMax, Float tiempoMax,
                                                 Integer caloriasMax, boolean vegetariana, boolean sinGluten,
                                                 Pageable pageable) {
        Pageable limitado = PageRequest.of(pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), TAMANYO_MAXIMO_PAGINA), pageable.getSort());
        Specification<Receta> filtro =
                RecetaSpecs.filtrar(texto, dificultadMax, tiempoMax, caloriasMax, vegetariana, sinGluten);
        return PaginaResponse.de(recetaRepository.findAll(filtro, limitado).map(recetaMapper::toResponse));
    }

    /** GET /recetas/{id} */
    @Transactional(readOnly = true)
    public RecetaDetalleResponse detalle(Integer id) {
        return construirDetalle(buscarActiva(id));
    }

    /** POST /recetas (solo CHEF). */
    @Transactional
    public RecetaDetalleResponse crear(Integer usuarioId, RecetaCompletaRequest request) {
        Usuario usuario = controlAcceso.usuario(usuarioId);
        controlAcceso.exigirRol(usuario, Roles.CHEF);

        Receta receta = recetaMapper.toEntity(request.receta());
        receta.setAutor(usuario);
        receta.setActiva(true);
        receta = recetaRepository.save(receta);

        guardarContenido(receta, request);
        return construirDetalle(receta);
    }

    /** PUT /recetas/{id} (CHEF autor o ADMIN). Sustituye pasos, ingredientes y fotos. */
    @Transactional
    public RecetaDetalleResponse modificar(Integer usuarioId, Integer id, RecetaCompletaRequest request) {
        Usuario usuario = controlAcceso.usuario(usuarioId);
        Receta receta = buscarActiva(id);
        exigirAutorOAdmin(usuario, receta);

        recetaMapper.updateEntity(receta, request.receta());

        pasoRepository.deleteByRecetaId(id);
        recetaIngredienteRepository.deleteByRecetaId(id);
        fotoRepository.deleteByRecetaId(id);
        recetaRepository.flush(); // los borrados se ejecutan antes de insertar el contenido nuevo

        guardarContenido(receta, request);
        return construirDetalle(receta);
    }

    /** DELETE /recetas/{id} (CHEF autor o ADMIN): baja lógica. */
    @Transactional
    public void retirar(Integer usuarioId, Integer id) {
        Usuario usuario = controlAcceso.usuario(usuarioId);
        Receta receta = buscarActiva(id);
        exigirAutorOAdmin(usuario, receta);
        receta.setActiva(false);
    }

    // ------------------------------------------------------------------

    private Receta buscarActiva(Integer id) {
        return recetaRepository.findByIdAndActivaTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada: " + id));
    }

    private void exigirAutorOAdmin(Usuario usuario, Receta receta) {
        if (controlAcceso.tieneRol(usuario, Roles.ADMIN)) {
            return;
        }
        boolean esAutor = controlAcceso.tieneRol(usuario, Roles.CHEF)
                && receta.getAutor() != null
                && receta.getAutor().getId().equals(usuario.getId());
        if (!esAutor) {
            throw new AccesoDenegadoException("Solo el autor o un administrador puede modificar esta receta");
        }
    }

    private RecetaDetalleResponse construirDetalle(Receta receta) {
        List<Paso> pasos = pasoRepository.findByRecetaIdOrderByNumeroAsc(receta.getId());
        List<RecetaIngrediente> ingredientes = recetaIngredienteRepository.findByRecetaId(receta.getId());
        List<Foto> fotos = fotoRepository.findByRecetaId(receta.getId());
        return recetaMapper.toDetalleResponse(receta, pasos, ingredientes, fotos);
    }

    private void guardarContenido(Receta receta, RecetaCompletaRequest request) {
        // Pasos: el número es la posición en la lista
        List<Paso> pasos = new ArrayList<>();
        int numero = 1;
        for (PasoItemRequest item : request.pasos()) {
            pasos.add(pasoMapper.toEntity(
                    new PasoRequest(receta.getId(), item.descripcion(), numero++), receta));
        }
        pasoRepository.saveAll(pasos);

        // Ingredientes: sin repetidos y todos existentes
        List<IngredienteItemRequest> items = request.ingredientes();
        Set<Integer> ids = items.stream().map(IngredienteItemRequest::idIngrediente).collect(Collectors.toSet());
        if (ids.size() != items.size()) {
            throw new ReglaNegocioException("Hay ingredientes repetidos en la receta");
        }
        Map<Integer, Ingrediente> encontrados = ingredienteRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Ingrediente::getId, Function.identity()));
        if (encontrados.size() != ids.size()) {
            throw new RecursoNoEncontradoException("Alguno de los ingredientes no existe");
        }
        List<RecetaIngrediente> lineas = new ArrayList<>();
        for (IngredienteItemRequest item : items) {
            lineas.add(recetaIngredienteMapper.toEntity(
                    new RecetaIngredienteRequest(receta.getId(), item.idIngrediente(), item.cantidad()),
                    receta, encontrados.get(item.idIngrediente())));
        }
        recetaIngredienteRepository.saveAll(lineas);

        // Fotos (opcionales)
        if (request.fotos() != null) {
            List<Foto> fotos = request.fotos().stream()
                    .map(url -> fotoMapper.toEntity(new FotoRequest(url, receta.getId()), receta))
                    .toList();
            fotoRepository.saveAll(fotos);
        }
    }
}
