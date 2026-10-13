package es.safareyes.smartchef.modelos;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Fetch;
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
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "rol")
    private Integer rol;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "contrasenya", nullable = false)
    private String contrasenya;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_preferencia",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_preferencia"))
    private Set<Preferencia> preferencias = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "lista_compra",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_ingrediente"))
    private Set<Ingrediente> listaCompra = new HashSet<>();

}
