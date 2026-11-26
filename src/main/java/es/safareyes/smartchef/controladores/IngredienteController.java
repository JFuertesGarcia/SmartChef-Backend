package es.safareyes.smartchef.controladores;

import es.safareyes.smartchef.dtos.IngredienteResponse;
import es.safareyes.smartchef.dtos.PaginaResponse;
import es.safareyes.smartchef.servicios.IngredienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ingredientes")
@RequiredArgsConstructor
public class IngredienteController {

    private final IngredienteService ingredienteService;

    /** Público. */
    @GetMapping
    public PaginaResponse<IngredienteResponse> listar(
            @RequestParam(required = false) String texto,
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ingredienteService.listar(texto, pageable);
    }
}
