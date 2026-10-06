package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.RecetaIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRecetaIngredienteRepository extends JpaRepository<RecetaIngrediente,Integer> {
}
