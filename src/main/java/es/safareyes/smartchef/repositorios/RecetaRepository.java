package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RecetaRepository extends JpaRepository<Receta, Integer>, JpaSpecificationExecutor<Receta> {

    Optional<Receta> findByIdAndActivaTrue(Integer id);
}
