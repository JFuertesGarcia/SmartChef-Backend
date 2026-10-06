package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IColeccionRepository extends JpaRepository<Coleccion,Integer> {
}
