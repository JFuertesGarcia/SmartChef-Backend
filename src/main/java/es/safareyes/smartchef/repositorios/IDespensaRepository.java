package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Despensa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IDespensaRepository extends JpaRepository<Despensa, Integer> {
    List<Despensa> findByUsuarioIdOrderByCaducidadAsc(Long usuarioId);
    Optional<Despensa> findByUsuarioIdAndIngredienteId(Long usuarioId, Long ingredienteId);
}
