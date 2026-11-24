package es.safareyes.smartchef.dtos;

import java.time.LocalDate;

public record DespensaResponse(
        Integer id,
        IngredienteResponse ingrediente,
        Integer idUsuario,
        Integer cantidad,
        LocalDate caducidad
) {}
