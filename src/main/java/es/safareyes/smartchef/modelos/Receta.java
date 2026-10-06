package es.safareyes.smartchef.modelos;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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


    @ManyToMany(mappedBy = "recetas")
    @ToString.Exclude
    private Set<Coleccion> colecciones = new HashSet<>();
}
