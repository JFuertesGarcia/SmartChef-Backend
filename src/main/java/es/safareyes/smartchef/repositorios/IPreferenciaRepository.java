package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Preferencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPreferenciaRepository extends JpaRepository<Preferencia,Integer> {
}
