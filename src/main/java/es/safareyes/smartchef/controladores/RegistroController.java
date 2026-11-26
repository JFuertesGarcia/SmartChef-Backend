package es.safareyes.smartchef.controladores;

import es.safareyes.smartchef.constantes.Cabeceras;
import es.safareyes.smartchef.dtos.RecetaUsuarioResponse;
import es.safareyes.smartchef.dtos.RegistroRequest;
import es.safareyes.smartchef.dtos.SemanaResponse;
import es.safareyes.smartchef.servicios.RegistroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/registros")
@RequiredArgsConstructor
public class RegistroController {

    private final RegistroService registroService;

    /** USUARIO. */
    @PostMapping
    public ResponseEntity<RecetaUsuarioResponse> registrar(
            @RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId,
            @Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registroService.registrar(usuarioId, request));
    }

    /** USUARIO. Fecha en formato yyyy-MM-dd; sin ella, la semana actual. */
    @GetMapping("/semana")
    public SemanaResponse semana(
            @RequestHeader(Cabeceras.USUARIO_ID) Integer usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return registroService.semana(usuarioId, fecha);
    }
}
