package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.*;
import java.util.Set;

public record ColeccionRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotNull(message = "El usuario es obligatorio")
        Integer idUsuario,

        Set<Integer> recetasIds
) {}
