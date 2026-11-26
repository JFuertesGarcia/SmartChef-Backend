package es.safareyes.smartchef.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "receta")
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "raciones")
    private Integer raciones;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "tiempo")
    private Float tiempo;

    @Column(name = "calorias")
    private Integer calorias;

    @Column(name = "dificultad")
    private Integer dificultad;

    @Column(name = "descripcion")
    private String descripcion;

    /** NUEVO: chef que creó la receta. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autor")
    @ToString.Exclude
    private Usuario autor;

    /** NUEVO: false = receta retirada (baja lógica). */
    @Column(name = "activa", nullable = false)
    private Boolean activa = true;

    @ManyToMany(mappedBy = "recetas", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Coleccion> colecciones = new HashSet<>();
}
