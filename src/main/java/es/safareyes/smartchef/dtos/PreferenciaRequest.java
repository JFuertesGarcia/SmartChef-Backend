package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.NotNull;

public record PreferenciaRequest(
        @NotNull(message = "El tipo es obligatorio")
        Integer tipo
) {}
