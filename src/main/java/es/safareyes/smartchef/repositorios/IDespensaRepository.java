package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Despensa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDespensaRepository extends JpaRepository<Despensa, Integer> {
}
