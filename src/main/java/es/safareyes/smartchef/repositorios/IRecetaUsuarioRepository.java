package es.safareyes.smartchef.repositorios;

import es.safareyes.smartchef.modelos.RecetaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRecetaUsuarioRepository extends JpaRepository<RecetaUsuario,Integer> {
}
