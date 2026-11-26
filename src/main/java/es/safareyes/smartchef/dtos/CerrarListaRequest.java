package es.safareyes.smartchef.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Lo comprado pasa a la despensa; puede ir vacío si no se compró nada. */
public record CerrarListaRequest(
        @NotNull(message = "La lista de comprados es obligatoria")
        @Valid
        List<ItemCompradoRequest> comprados
) {}
