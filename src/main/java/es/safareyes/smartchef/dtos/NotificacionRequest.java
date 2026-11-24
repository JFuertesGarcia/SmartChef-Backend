package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.*;

public record NotificacionRequest(
        @NotNull(message = "El tipo es obligatorio")
        Integer tipo,

        @NotBlank(message = "El texto es obligatorio")
        String texto
) {}
