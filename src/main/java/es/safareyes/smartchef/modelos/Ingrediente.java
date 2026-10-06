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
@Table(name = "ingrediente")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "vegetariano")
    private Boolean vegetariano;

    @Column(name = "sin_gluten")
    private Boolean sinGluten;

    @Column(name = "calorias")
    private Integer calorias;

    @Column(name = "tipo_cantidad")
    private Integer tipoCantidad;

    @Column(name = "precio")
    private Float precio;

}
