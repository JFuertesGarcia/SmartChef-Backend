package es.safareyes.smartchef.dtos;

import java.time.LocalDate;
import java.util.List;

public record SemanaResponse(
        LocalDate desde,
        LocalDate hasta,
        List<DiaRegistroResponse> dias,
        int caloriasTotales
) {}
