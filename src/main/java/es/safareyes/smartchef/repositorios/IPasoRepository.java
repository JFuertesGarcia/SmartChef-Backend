package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Paso;
import es.safareyes.smartchef.modelos.Receta;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPasoRepository extends JpaRepository<Paso,Integer> {
    List<Paso> findAllByRecetaEqualsOrderByNumeroAsc(Receta receta);
}
