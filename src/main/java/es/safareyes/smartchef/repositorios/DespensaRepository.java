package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Despensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DespensaRepository extends JpaRepository<Despensa, Integer> {

    /** Despensa del usuario: primero lo que caduca antes; lo que no caduca, al final. */
    @Query("""
            select d from Despensa d
              join fetch d.ingrediente i
            where d.usuario.id = :idUsuario
            order by d.caducidad asc nulls last, i.nombre asc
            """)
    List<Despensa> findByUsuarioOrdenada(@Param("idUsuario") Integer idUsuario);

    Optional<Despensa> findByUsuarioIdAndIngredienteId(Integer idUsuario, Integer idIngrediente);

    /** Ids de ingredientes con existencias y sin caducar. */
    @Query("""
            select d.ingrediente.id from Despensa d
            where d.usuario.id = :idUsuario
              and d.cantidad > 0
              and (d.caducidad is null or d.caducidad >= :hoy)
            """)
    List<Integer> findIdsIngredientesVigentes(@Param("idUsuario") Integer idUsuario,
                                              @Param("hoy") LocalDate hoy);
}
