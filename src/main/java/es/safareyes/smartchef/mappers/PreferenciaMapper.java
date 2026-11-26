package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.PreferenciaRequest;
import es.safareyes.smartchef.dtos.PreferenciaResponse;
import es.safareyes.smartchef.modelos.Preferencia;
import org.springframework.stereotype.Component;

@Component
public class PreferenciaMapper {

    public Preferencia toEntity(PreferenciaRequest request) {
        Preferencia preferencia = new Preferencia();
        updateEntity(preferencia, request);
        return preferencia;
    }

    public void updateEntity(Preferencia preferencia, PreferenciaRequest request) {
        preferencia.setTipo(request.tipo());
    }

    public PreferenciaResponse toResponse(Preferencia preferencia) {
        return new PreferenciaResponse(
                preferencia.getId(),
                preferencia.getTipo()
        );
    }
}
