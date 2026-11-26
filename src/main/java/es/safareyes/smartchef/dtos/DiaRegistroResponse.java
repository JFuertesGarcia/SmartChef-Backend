package es.safareyes.smartchef.dtos;

import java.time.LocalDate;
import java.util.List;

public record DiaRegistroResponse(
        LocalDate fecha,
        List<RegistroItemResponse> registros,
        int calorias
) {}
