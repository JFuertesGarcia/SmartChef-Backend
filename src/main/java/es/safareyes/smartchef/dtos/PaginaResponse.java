package es.safareyes.smartchef.dtos;

import org.springframework.data.domain.Page;
import java.util.List;

public record PaginaResponse<T>(
        List<T> contenido,
        int pagina,
        int tamanyo,
        long totalElementos,
        int totalPaginas
) {
    public static <T> PaginaResponse<T> de(Page<T> page) {
        return new PaginaResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
