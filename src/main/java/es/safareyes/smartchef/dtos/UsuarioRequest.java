package es.safareyes.smartchef.dtos;

import jakarta.validation.constraints.*;
import java.util.Set;

public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String contrasenya,

        Set<Integer> preferenciasIds
) {}
