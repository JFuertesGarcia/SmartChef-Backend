package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Receta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IRecetaRepository extends JpaRepository<Receta, Long>, JpaSpecificationExecutor<Receta> {

    // ==========================================
    // Q1: Métodos derivados del nombre (Query Methods) (Mínimo 5)
    // ==========================================

    List<Receta> findByNombreContainingIgnoreCase(String nombre);

    List<Receta> findByRacionesBetween(int min, int max);

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Receta> findByIdAndRacionesGreaterThan(Long id, int raciones);

    List<Receta> findTop10ByOrderByDificultadAsc();


    // ==========================================
    // Q2: JPQL con @Query y parámetros con nombre (@Param) (Mínimo 3)
    // ==========================================

    // 6. Recetas vegetarianas (comprobando que sus ingredientes asociados en receta_ingrediente sean vegetarianos)
    @Query("SELECT r FROM Receta r WHERE :esVegetariano = true AND NOT EXISTS (SELECT ri FROM RecetaIngrediente ri WHERE ri.receta = r AND ri.ingrediente.vegetariano = false)")
    List<Receta> findRecetasVegetarianas(@Param("esVegetariano") boolean esVegetariano);

    // 7. Calcular el coste total por ración usando JOIN y funciones de agregado (SUM)
    @Query("SELECT SUM(ri.cantidad * i.precio) / r.raciones FROM Receta r JOIN RecetaIngrediente ri ON ri.receta = r JOIN Ingrediente i ON ri.ingrediente = i WHERE r.id = :recetaId")
    Double calcularCostePorRacion(@Param("recetaId") Long recetaId);

    // 8. Recetas más cocinadas usando COUNT y GROUP BY ordenadas de forma descendente
    @Query("SELECT r, COUNT(ru) as total FROM Receta r JOIN RecetaUsuario ru ON ru.receta = r WHERE ru.fecha BETWEEN :desde AND :hasta GROUP BY r ORDER BY total DESC")
    List<Receta> findRecetasMasCocinadas(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, Pageable pageable);


    // ==========================================
    // Q3: SQL nativo con @Query(nativeQuery = true) (Mínimo 2)
    // ==========================================

    // 9. Coincidencia con la despensa en porcentaje usando GROUP BY y HAVING
    @Query(value = "SELECT r.id, r.nombre, (COUNT(ri.id_ingrediente) * 100.0 / (SELECT COUNT(*) FROM receta_ingrediente WHERE id_receta = r.id)) as porcentaje " +
            "FROM receta r JOIN receta_ingrediente ri ON r.id = ri.id_receta " +
            "WHERE ri.id_ingrediente IN (:ingredientesIds) " +
            "GROUP BY r.id, r.nombre " +
            "HAVING (COUNT(ri.id_ingrediente) * 100.0 / (SELECT COUNT(*) FROM receta_ingrediente WHERE id_receta = r.id)) > 50 " +
            "ORDER BY porcentaje DESC", nativeQuery = true)
    List<Object[]> findRecetasPorcentajeDespensa(@Param("ingredientesIds") List<Long> ingredientesIds);

    // 10. Calorías por día de una semana utilizando funciones de fecha de PostgreSQL (DATE_TRUNC)
    @Query(value = "SELECT DATE_TRUNC('week', fecha) as semana, SUM(r.calorias) as totalCalorias " +
            "FROM receta_usuario ru JOIN receta r ON ru.id_receta = r.id " +
            "WHERE ru.id_usuario = :usuarioId AND ru.fecha BETWEEN :desde AND :hasta " +
            "GROUP BY semana ORDER BY semana ASC", nativeQuery = true)
    List<Object[]> findCaloriasPorSemanaNativa(@Param("usuarioId") Long usuarioId, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);


    // ==========================================
    // Q4: Consultas de modificación con @Modifying (Mínimo 1)
    // ==========================================

    // 11. Eliminar de la despensa los ingredientes caducados hace más de 7 días
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM despensa WHERE caducidad < :fechaLimite", nativeQuery = true)
    void eliminarIngredientesCaducados(@Param("fechaLimite") LocalDateTime fechaLimite);
    
    // ==========================================
    // Q6: Paginación y ordenación (Mínimo 1)
    // ==========================================

    // 13. Catálogo paginado de recetas
    Page<Receta> findAll(Pageable pageable);


    // ==========================================
    // Q8: Evitar el problema N+1 (Mínimo 1)
    // ==========================================

    // 14. Cargar una receta con sus relaciones clave mediante @EntityGraph
    @EntityGraph(attributePaths = {"pasos"})
    Optional<Receta> findWithDetallesById(Long id);
}