package es.safareyes.smartchef.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record RecetaCompletaRequest(
        @NotNull(message = "Los datos de la receta son obligatorios")
        @Valid
        RecetaRequest receta,

        @NotEmpty(message = "La receta necesita al menos un paso")
        @Valid
        List<PasoItemRequest> pasos,

        @NotEmpty(message = "La receta necesita al menos un ingrediente")
        @Valid
        List<IngredienteItemRequest> ingredientes,

        List<@NotBlank @Size(max = 500, message = "La URL no puede superar los 500 caracteres") String> fotos
) {}
