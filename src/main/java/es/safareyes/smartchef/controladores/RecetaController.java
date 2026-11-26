package es.safareyes.smartchef.controladores;

import es.safareyes.smartchef.constantes.Cabeceras;
import es.safareyes.smartchef.dtos.BusquedaPorIngredientesRequest;
import es.safareyes.smartchef.dtos.PaginaResponse;
import es.safareyes.smartchef.dtos.PropuestaRecetaResponse;
import es.safareyes.smartchef.dtos.RecetaCompletaRequest;
import es.safareyes.smartchef.dtos.RecetaDetalleResponse;
import es.safareyes.smartchef.dtos.RecetaResponse;
import es.safareyes.smartchef.servicios.PropuestaRecetaService;
import es.safareyes.smartchef.servicios.RecetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;
    private final PropuestaRecetaService propuestaRecetaService;

    /** Público. */
    @GetMapping
    public PaginaResponse<RecetaResponse> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Integer dificultadMax,
            @RequestParam(required = false) Float tiempoMax,
            @RequestParam(required = false) Integer caloriasMax,
            @RequestParam(defaultValue = "false") boolean vegetariana,
            @RequestParam(defaultValue = "false") boolean sinGluten,
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return recetaService.listar(texto, dificultadMax, tiempoMax, caloriasMax, vegetariana, sinGluten, pageable);
    }

    /** Público. */
    @GetMapping("/{id}")
    public RecetaDetalleResponse detalle(@PathVariable Integer id) {
        return recetaService.detalle(id);
    }

    /** CHEF. */
    @PostMapping
    public ResponseEntity<RecetaDetalleResponse> crear(
            @RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId,
            @Valid @RequestBody RecetaCompletaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaService.crear(usuarioId, request));
    }

    /** CHEF (autor) o ADMIN. */
    @PutMapping("/{id}")
    public RecetaDetalleResponse modificar(
            @RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId,
            @PathVariable Integer id,
            @Valid @RequestBody RecetaCompletaRequest request) {
        return recetaService.modificar(usuarioId, id, request);
    }

    /** CHEF (autor) o ADMIN. Baja lógica. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> retirar(
            @RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId,
            @PathVariable Integer id) {
        recetaService.retirar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    /** USUARIO. El cuerpo es opcional: sin él se usa la despensa. */
    @PostMapping("/buscar-por-ingredientes")
    public List<PropuestaRecetaResponse> buscarPorIngredientes(
            @RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId,
            @RequestBody(required = false) BusquedaPorIngredientesRequest request) {
        return propuestaRecetaService.proponer(usuarioId, request);
    }
}
