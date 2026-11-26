package es.safareyes.smartchef.controladores;

import es.safareyes.smartchef.constantes.Cabeceras;
import es.safareyes.smartchef.dtos.InformeRecetasPopularesResponse;
import es.safareyes.smartchef.servicios.InformeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/informes")
@RequiredArgsConstructor
public class InformeController {

    private final InformeService informeService;

    /** ADMIN, CHEF. Fechas yyyy-MM-dd; por defecto, los últimos 30 días. */
    @GetMapping("/recetas-populares")
    public InformeRecetasPopularesResponse recetasPopulares(
            @RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return informeService.recetasPopulares(usuarioId, desde, hasta);
    }
}
