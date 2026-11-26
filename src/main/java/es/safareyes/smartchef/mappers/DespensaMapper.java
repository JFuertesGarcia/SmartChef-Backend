package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.DespensaRequest;
import es.safareyes.smartchef.dtos.DespensaResponse;
import es.safareyes.smartchef.modelos.Despensa;
import es.safareyes.smartchef.modelos.Ingrediente;
import es.safareyes.smartchef.modelos.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DespensaMapper {

    private final IngredienteMapper ingredienteMapper;

    /** Ingrediente y usuario se buscan antes en el servicio con sus ids. */
    public Despensa toEntity(DespensaRequest request, Ingrediente ingrediente, Usuario usuario) {
        Despensa despensa = new Despensa();
        updateEntity(despensa, request, ingrediente, usuario);
        return despensa;
    }

    public void updateEntity(Despensa despensa, DespensaRequest request,
                             Ingrediente ingrediente, Usuario usuario) {
        despensa.setIngrediente(ingrediente);
        despensa.setUsuario(usuario);
        despensa.setCantidad(request.cantidad());
        despensa.setCaducidad(request.caducidad());
    }

    public DespensaResponse toResponse(Despensa despensa) {
        return new DespensaResponse(
                despensa.getId(),
                ingredienteMapper.toResponse(despensa.getIngrediente()),
                despensa.getUsuario() != null ? despensa.getUsuario().getId() : null,
                despensa.getCantidad(),
                despensa.getCaducidad()
        );
    }
}
