package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Ingrediente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Integer> {

    Page<Ingrediente> findByNombreContainingIgnoreCase(String texto, Pageable pageable);
}
