package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ColeccionRepository extends JpaRepository<Coleccion, Integer> {

    @Query("""
            select distinct c from Coleccion c
              left join fetch c.recetas
            where c.usuario.id = :idUsuario
            order by c.nombre
            """)
    List<Coleccion> findByUsuarioConRecetas(@Param("idUsuario") Integer idUsuario);

    Optional<Coleccion> findByUsuarioIdAndNombreIgnoreCase(Integer idUsuario, String nombre);
}
