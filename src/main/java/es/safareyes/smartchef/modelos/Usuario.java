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
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "rol")
    private Integer rol;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "contrasenya")
    @ToString.Exclude
    private String contrasenya;

    @ManyToMany
    @JoinTable(
            name = "usuario_preferencia",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_preferencia"))
    @ToString.Exclude
    private Set<Preferencia> preferencias = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "lista_compra",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_ingrediente"))
    @ToString.Exclude
    private Set<Ingrediente> listaCompra = new HashSet<>();

}
