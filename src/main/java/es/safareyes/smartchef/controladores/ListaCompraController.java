package es.safareyes.smartchef.controladores;

import es.safareyes.smartchef.constantes.Cabeceras;
import es.safareyes.smartchef.dtos.CerrarListaRequest;
import es.safareyes.smartchef.dtos.DespensaResponse;
import es.safareyes.smartchef.dtos.ListaCompraDesdeRecetaRequest;
import es.safareyes.smartchef.dtos.ListaCompraResponse;
import es.safareyes.smartchef.servicios.ListaCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/listas-compra")
@RequiredArgsConstructor
public class ListaCompraController {

    private final ListaCompraService listaCompraService;

    /** USUARIO. */
    @PostMapping("/desde-receta")
    public ListaCompraResponse desdeReceta(
            @RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId,
            @Valid @RequestBody ListaCompraDesdeRecetaRequest request) {
        return listaCompraService.anyadirDesdeReceta(usuarioId, request.idReceta());
    }

    /** USUARIO (propia). Devuelve las entradas de la despensa que se han actualizado. */
    @PatchMapping("/{id}/cerrar")
    public List<DespensaResponse> cerrar(
            @RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId,
            @PathVariable Integer id,
            @Valid @RequestBody CerrarListaRequest request) {
        return listaCompraService.cerrar(usuarioId, id, request);
    }
}
