package es.safareyes.smartchef.controladores;

import es.safareyes.smartchef.constantes.Cabeceras;
import es.safareyes.smartchef.dtos.DespensaItemRequest;
import es.safareyes.smartchef.dtos.DespensaResponse;
import es.safareyes.smartchef.servicios.DespensaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/despensa")
@RequiredArgsConstructor
public class DespensaController {

    private final DespensaService despensaService;

    /** USUARIO. */
    @GetMapping
    public List<DespensaResponse> listar(@RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId) {
        return despensaService.listar(usuarioId);
    }

    /** USUARIO. 200 con el ingrediente guardado, o 204 si la cantidad era 0 y se eliminó. */
    @PutMapping("/{ingredienteId}")
    public ResponseEntity<DespensaResponse> guardar(
            @RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId,
            @PathVariable Integer ingredienteId,
            @Valid @RequestBody DespensaItemRequest request) {
        return despensaService.guardar(usuarioId, ingredienteId, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
