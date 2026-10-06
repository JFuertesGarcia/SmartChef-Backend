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
@Table(name = "receta_ingrediente")
public class RecetaIngrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ingrediente")
    @ToString.Exclude
    private Ingrediente ingrediente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receta")
    @ToString.Exclude
    private Receta receta;

    @Column(name = "cantidad")
    private Integer cantidad;
}
