package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.RecetaUsuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecetaUsuarioRepository extends JpaRepository<RecetaUsuario, Integer> {

    /** Registros de un usuario en el intervalo [desde, hasta). */
    @Query("""
            select ru from RecetaUsuario ru
              join fetch ru.receta
            where ru.usuario.id = :idUsuario
              and ru.tipo = :tipo
              and ru.fecha >= :desde
              and ru.fecha < :hasta
            order by ru.fecha
            """)
    List<RecetaUsuario> findDelUsuarioEntre(@Param("idUsuario") Integer idUsuario,
                                            @Param("tipo") Integer tipo,
                                            @Param("desde") LocalDateTime desde,
                                            @Param("hasta") LocalDateTime hasta);

    /** Recetas más registradas del tipo dado en el intervalo [desde, hasta). */
    @Query("""
            select ru.receta.id as recetaId, count(ru) as total
            from RecetaUsuario ru
            where ru.tipo = :tipo
              and ru.fecha >= :desde
              and ru.fecha < :hasta
            group by ru.receta.id
            order by count(ru) desc, ru.receta.id asc
            """)
    List<RecetaConteo> contarPorReceta(@Param("tipo") Integer tipo,
                                       @Param("desde") LocalDateTime desde,
                                       @Param("hasta") LocalDateTime hasta,
                                       Pageable pageable);
}
