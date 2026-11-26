package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.NotBlank;

/** El número de paso se asigna según la posición en la lista. */
public record PasoItemRequest(
        @NotBlank(message = "La descripción del paso es obligatoria")
        String descripcion
) {}
