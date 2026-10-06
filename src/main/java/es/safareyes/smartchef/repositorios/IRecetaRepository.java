package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Paso;
import es.safareyes.smartchef.modelos.Receta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRecetaRepository extends JpaRepository<Receta,Integer> {
}
