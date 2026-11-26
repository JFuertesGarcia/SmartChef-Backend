package es.safareyes.smartchef.controladores;

import es.safareyes.smartchef.constantes.Cabeceras;
import es.safareyes.smartchef.dtos.ColeccionResponse;
import es.safareyes.smartchef.servicios.ColeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/colecciones")
@RequiredArgsConstructor
public class ColeccionController {

    private final ColeccionService coleccionService;

    /** USUARIO. */
    @GetMapping
    public List<ColeccionResponse> listar(@RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId) {
        return coleccionService.listar(usuarioId);
    }

    /** USUARIO. Crea la colección si no existe. */
    @PutMapping("/{nombre}/recetas/{recetaId}")
    public ColeccionResponse guardarReceta(
            @RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId,
            @PathVariable String nombre,
            @PathVariable Integer recetaId) {
        return coleccionService.guardarReceta(usuarioId, nombre, recetaId);
    }
}
