package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INotificacionRepository extends JpaRepository<Notificacion,Integer> {
}
