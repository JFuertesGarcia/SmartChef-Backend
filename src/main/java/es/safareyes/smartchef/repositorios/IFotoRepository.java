package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFotoRepository extends JpaRepository<Foto,Integer> {
}
