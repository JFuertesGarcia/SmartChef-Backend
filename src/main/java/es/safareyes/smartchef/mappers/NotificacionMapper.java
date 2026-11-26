package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.NotificacionRequest;
import es.safareyes.smartchef.dtos.NotificacionResponse;
import es.safareyes.smartchef.modelos.Notificacion;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMapper {

    public Notificacion toEntity(NotificacionRequest request) {
        Notificacion notificacion = new Notificacion();
        updateEntity(notificacion, request);
        return notificacion;
    }

    public void updateEntity(Notificacion notificacion, NotificacionRequest request) {
        notificacion.setTipo(request.tipo());
        notificacion.setTexto(request.texto());
    }

    public NotificacionResponse toResponse(Notificacion notificacion) {
        return new NotificacionResponse(
                notificacion.getId(),
                notificacion.getTipo(),
                notificacion.getTexto()
        );
    }
}
