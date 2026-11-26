package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.RecetaIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RecetaIngredienteRepository extends JpaRepository<RecetaIngrediente, Integer> {

    @Query("""
            select ri from RecetaIngrediente ri
              join fetch ri.ingrediente
            where ri.receta.id = :idReceta
            """)
    List<RecetaIngrediente> findByRecetaId(@Param("idReceta") Integer idReceta);

    void deleteByRecetaId(Integer idReceta);

    /** Todas las filas de las recetas activas que contienen al menos uno de los ingredientes dados. */
    @Query("""
            select ri from RecetaIngrediente ri
              join fetch ri.ingrediente
              join fetch ri.receta r
            where r.activa = true
              and r.id in (select ri2.receta.id from RecetaIngrediente ri2
                           where ri2.ingrediente.id in :ids)
            """)
    List<RecetaIngrediente> findDeRecetasConAlgunIngrediente(@Param("ids") Collection<Integer> ids);
}
