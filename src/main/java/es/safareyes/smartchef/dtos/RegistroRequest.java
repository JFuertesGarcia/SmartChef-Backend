package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Si no se envía fecha se usa el momento actual. */
public record RegistroRequest(
        @NotNull(message = "La receta es obligatoria")
        Integer idReceta,

        LocalDateTime fecha
) {}
