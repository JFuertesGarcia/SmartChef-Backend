package es.safareyes.smartchef.dtos;

import java.time.LocalDate;
import java.util.List;

public record InformeRecetasPopularesResponse(
        LocalDate desde,
        LocalDate hasta,
        List<RecetaPopularResponse> masCocinadas,
        List<RecetaPopularResponse> masGuardadas
) {}
