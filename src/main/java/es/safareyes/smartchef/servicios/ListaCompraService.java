package es.safareyes.smartchef.servicios;

import es.safareyes.smartchef.dtos.CerrarListaRequest;
import es.safareyes.smartchef.dtos.DespensaRequest;
import es.safareyes.smartchef.dtos.DespensaResponse;
import es.safareyes.smartchef.dtos.IngredienteResponse;
import es.safareyes.smartchef.dtos.ItemCompradoRequest;
import es.safareyes.smartchef.dtos.ListaCompraResponse;
import es.safareyes.smartchef.excepciones.AccesoDenegadoException;
import es.safareyes.smartchef.excepciones.RecursoNoEncontradoException;
import es.safareyes.smartchef.excepciones.ReglaNegocioException;
import es.safareyes.smartchef.mappers.DespensaMapper;
import es.safareyes.smartchef.mappers.IngredienteMapper;
import es.safareyes.smartchef.modelos.Despensa;
import es.safareyes.smartchef.modelos.Ingrediente;
import es.safareyes.smartchef.modelos.RecetaIngrediente;
import es.safareyes.smartchef.modelos.Usuario;
import es.safareyes.smartchef.repositorios.DespensaRepository;
import es.safareyes.smartchef.repositorios.RecetaIngredienteRepository;
import es.safareyes.smartchef.repositorios.RecetaRepository;
import es.safareyes.smartchef.seguridad.ControlAcceso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Lista de la compra. Con el modelo actual (Usuario.listaCompra) hay UNA lista por usuario,
 * sin cantidades ni estado, así que "{id}" de /listas-compra/{id}/cerrar es el id del usuario.
 */
@Service
@RequiredArgsConstructor
public class ListaCompraService {

    private final RecetaRepository recetaRepository;
    private final RecetaIngredienteRepository recetaIngredienteRepository;
    private final DespensaRepository despensaRepository;
    private final DespensaMapper despensaMapper;
    private final IngredienteMapper ingredienteMapper;
    private final ControlAcceso controlAcceso;

    /** POST /listas-compra/desde-receta (RN-05): añade lo que falta en la despensa para cocinar la receta. */
    @Transactional
    public ListaCompraResponse anyadirDesdeReceta(Integer usuarioId, Integer idReceta) {
        Usuario usuario = controlAcceso.usuario(usuarioId);
        recetaRepository.findByIdAndActivaTrue(idReceta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada: " + idReceta));

        Map<Integer, Despensa> enDespensa = despensaRepository.findByUsuarioOrdenada(usuarioId).stream()
                .collect(Collectors.toMap(d -> d.getIngrediente().getId(), Function.identity(), (a, b) -> a));
        LocalDate hoy = LocalDate.now();

        int anyadidos = 0;
        for (RecetaIngrediente necesario : recetaIngredienteRepository.findByRecetaId(idReceta)) {
            Ingrediente ingrediente = necesario.getIngrediente();
            if (falta(necesario, enDespensa.get(ingrediente.getId()), hoy)
                    && usuario.getListaCompra().add(ingrediente)) {
                anyadidos++;
            }
        }
        return respuesta(usuario, anyadidos);
    }

    /** PATCH /listas-compra/{id}/cerrar: lo comprado pasa a la despensa y la lista se vacía. */
    @Transactional
    public List<DespensaResponse> cerrar(Integer usuarioId, Integer id, CerrarListaRequest request) {
        if (!id.equals(usuarioId)) {
            throw new AccesoDenegadoException("Solo puedes cerrar tu propia lista");
        }
        Usuario usuario = controlAcceso.usuario(usuarioId);
        if (usuario.getListaCompra().isEmpty()) {
            throw new ReglaNegocioException("La lista de la compra está vacía");
        }

        Map<Integer, Ingrediente> enLista = usuario.getListaCompra().stream()
                .collect(Collectors.toMap(Ingrediente::getId, Function.identity()));

        List<Despensa> actualizadas = new ArrayList<>();
        for (ItemCompradoRequest item : request.comprados()) {
            Ingrediente ingrediente = enLista.get(item.idIngrediente());
            if (ingrediente == null) {
                throw new ReglaNegocioException("El ingrediente " + item.idIngrediente() + " no está en la lista");
            }
            Despensa despensa = despensaRepository
                    .findByUsuarioIdAndIngredienteId(usuarioId, item.idIngrediente())
                    .orElseGet(Despensa::new);
            int actual = despensa.getCantidad() != null ? despensa.getCantidad() : 0;
            LocalDate caducidad = item.caducidad() != null ? item.caducidad() : despensa.getCaducidad();
            despensaMapper.updateEntity(despensa,
                    new DespensaRequest(ingrediente.getId(), usuarioId, actual + item.cantidad(), caducidad),
                    ingrediente, usuario);
            actualizadas.add(despensaRepository.save(despensa));
        }

        usuario.getListaCompra().clear();
        return actualizadas.stream().map(despensaMapper::toResponse).toList();
    }

    // ------------------------------------------------------------------

    /** Falta si no está en la despensa, está caducado o hay menos cantidad de la necesaria. */
    private boolean falta(RecetaIngrediente necesario, Despensa enDespensa, LocalDate hoy) {
        if (enDespensa == null) {
            return true;
        }
        if (enDespensa.getCaducidad() != null && enDespensa.getCaducidad().isBefore(hoy)) {
            return true;
        }
        int tiene = enDespensa.getCantidad() != null ? enDespensa.getCantidad() : 0;
        int requiere = necesario.getCantidad() != null ? necesario.getCantidad() : 0;
        return tiene < requiere;
    }

    private ListaCompraResponse respuesta(Usuario usuario, int anyadidos) {
        List<IngredienteResponse> ingredientes = usuario.getListaCompra().stream()
                .sorted(Comparator.comparing(Ingrediente::getNombre, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(ingredienteMapper::toResponse)
                .toList();
        return new ListaCompraResponse(ingredientes, anyadidos);
    }
}
