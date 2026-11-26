package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ItemCompradoRequest(
        @NotNull(message = "El ingrediente es obligatorio")
        Integer idIngrediente,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que 0")
        Integer cantidad,

        LocalDate caducidad
) {}
