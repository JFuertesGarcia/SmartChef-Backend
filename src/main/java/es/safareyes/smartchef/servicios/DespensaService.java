package es.safareyes.smartchef.servicios;

import es.safareyes.smartchef.dtos.DespensaItemRequest;
import es.safareyes.smartchef.dtos.DespensaRequest;
import es.safareyes.smartchef.dtos.DespensaResponse;
import es.safareyes.smartchef.excepciones.RecursoNoEncontradoException;
import es.safareyes.smartchef.mappers.DespensaMapper;
import es.safareyes.smartchef.modelos.Despensa;
import es.safareyes.smartchef.modelos.Ingrediente;
import es.safareyes.smartchef.modelos.Usuario;
import es.safareyes.smartchef.repositorios.DespensaRepository;
import es.safareyes.smartchef.repositorios.IngredienteRepository;
import es.safareyes.smartchef.seguridad.ControlAcceso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DespensaService {

    private final DespensaRepository despensaRepository;
    private final IngredienteRepository ingredienteRepository;
    private final DespensaMapper despensaMapper;
    private final ControlAcceso controlAcceso;

    /** GET /despensa: los próximos a caducar primero. */
    @Transactional(readOnly = true)
    public List<DespensaResponse> listar(Integer usuarioId) {
        controlAcceso.usuario(usuarioId);
        return despensaRepository.findByUsuarioOrdenada(usuarioId).stream()
                .map(despensaMapper::toResponse)
                .toList();
    }

    /**
     * PUT /despensa/{ingredienteId}: añade o actualiza cantidad y caducidad.
     * Con cantidad 0 elimina el ingrediente y devuelve vacío.
     */
    @Transactional
    public Optional<DespensaResponse> guardar(Integer usuarioId, Integer ingredienteId, DespensaItemRequest request) {
        Usuario usuario = controlAcceso.usuario(usuarioId);
        Optional<Despensa> existente = despensaRepository.findByUsuarioIdAndIngredienteId(usuarioId, ingredienteId);

        if (request.cantidad() == 0) {
            existente.ifPresent(despensaRepository::delete);
            return Optional.empty();
        }

        Ingrediente ingrediente = ingredienteRepository.findById(ingredienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ingrediente no encontrado: " + ingredienteId));

        Despensa despensa = existente.orElseGet(Despensa::new);
        despensaMapper.updateEntity(despensa,
                new DespensaRequest(ingredienteId, usuarioId, request.cantidad(), request.caducidad()),
                ingrediente, usuario);
        return Optional.of(despensaMapper.toResponse(despensaRepository.save(despensa)));
    }
}
