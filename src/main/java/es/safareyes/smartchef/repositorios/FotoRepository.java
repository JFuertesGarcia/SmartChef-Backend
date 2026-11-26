package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FotoRepository extends JpaRepository<Foto, Integer> {

    List<Foto> findByRecetaId(Integer idReceta);

    void deleteByRecetaId(Integer idReceta);
}
