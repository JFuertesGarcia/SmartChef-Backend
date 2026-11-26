package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.NotNull;

public record ListaCompraDesdeRecetaRequest(
        @NotNull(message = "La receta es obligatoria")
        Integer idReceta
) {}
