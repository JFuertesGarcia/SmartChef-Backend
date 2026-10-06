package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IIngredienteRepository extends JpaRepository<Ingrediente,Integer>{
}
