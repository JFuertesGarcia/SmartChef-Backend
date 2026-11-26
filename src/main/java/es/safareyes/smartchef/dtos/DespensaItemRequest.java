package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/** Cantidad 0 elimina el ingrediente de la despensa. */
public record DespensaItemRequest(
        @NotNull(message = "La cantidad es obligatoria")
        @PositiveOrZero(message = "La cantidad no puede ser negativa")
        Integer cantidad,

        LocalDate caducidad
) {}
