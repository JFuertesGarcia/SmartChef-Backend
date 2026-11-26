package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.Ingrediente;
import es.safareyes.smartchef.modelos.Receta;
import es.safareyes.smartchef.modelos.RecetaIngrediente;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/** Filtros dinámicos del catálogo de recetas (RF-02). Solo incluye recetas activas. */
public final class RecetaSpecs {

    private RecetaSpecs() {}

    public static Specification<Receta> filtrar(String texto,
                                                Integer dificultadMax,
                                                Float tiempoMax,
                                                Integer caloriasMax,
                                                boolean vegetariana,
                                                boolean sinGluten) {
        return (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();
            condiciones.add(cb.isTrue(root.get("activa")));

            if (texto != null && !texto.isBlank()) {
                condiciones.add(cb.like(cb.lower(root.get("nombre")),
                        "%" + texto.trim().toLowerCase() + "%"));
            }
            if (dificultadMax != null) {
                condiciones.add(cb.lessThanOrEqualTo(root.<Integer>get("dificultad"), dificultadMax));
            }
            if (tiempoMax != null) {
                condiciones.add(cb.lessThanOrEqualTo(root.<Float>get("tiempo"), tiempoMax));
            }
            if (caloriasMax != null) {
                condiciones.add(cb.lessThanOrEqualTo(root.<Integer>get("calorias"), caloriasMax));
            }
            if (vegetariana) {
                condiciones.add(todosLosIngredientes(root, query, cb, "vegetariano"));
            }
            if (sinGluten) {
                condiciones.add(todosLosIngredientes(root, query, cb, "sinGluten"));
            }
            return cb.and(condiciones.toArray(new Predicate[0]));
        };
    }

    /** La receta no tiene ningún ingrediente con ese atributo a false o sin informar. */
    private static Predicate todosLosIngredientes(Root<Receta> root, CriteriaQuery<?> query,
                                                  CriteriaBuilder cb, String atributo) {
        Subquery<Integer> sub = query.subquery(Integer.class);
        Root<RecetaIngrediente> ri = sub.from(RecetaIngrediente.class);
        Join<RecetaIngrediente, Ingrediente> ingrediente = ri.join("ingrediente");
        sub.select(ri.<Integer>get("id"))
                .where(cb.equal(ri.get("receta"), root),
                        cb.or(cb.isNull(ingrediente.get(atributo)),
                                cb.isFalse(ingrediente.<Boolean>get(atributo))));
        return cb.not(cb.exists(sub));
    }
}
