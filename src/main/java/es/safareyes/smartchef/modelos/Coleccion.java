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
@EqualsAndHashCode
@Table(name = "coleccion")
public class Coleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "nombre")
    private String nombre;

    @ManyToMany
    @JoinTable(
            name = "receta_coleccion",
            joinColumns = @JoinColumn(name = "id_coleccion"),
            inverseJoinColumns = @JoinColumn(name = "id_receta"))
    private Set<Receta> recetas = new HashSet<>();
}
