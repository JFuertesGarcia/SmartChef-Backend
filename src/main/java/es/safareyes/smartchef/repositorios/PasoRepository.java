package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Paso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasoRepository extends JpaRepository<Paso, Integer> {

    List<Paso> findByRecetaIdOrderByNumeroAsc(Integer idReceta);

    void deleteByRecetaId(Integer idReceta);
}
