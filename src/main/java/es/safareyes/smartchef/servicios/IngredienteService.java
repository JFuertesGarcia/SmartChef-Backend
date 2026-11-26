package es.safareyes.smartchef.servicios;

import es.safareyes.smartchef.dtos.IngredienteResponse;
import es.safareyes.smartchef.dtos.PaginaResponse;
import es.safareyes.smartchef.mappers.IngredienteMapper;
import es.safareyes.smartchef.repositorios.IngredienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IngredienteService {

    private static final int TAMANYO_MAXIMO_PAGINA = 100;

    private final IngredienteRepository ingredienteRepository;
    private final IngredienteMapper ingredienteMapper;

    /** GET /ingredientes?texto= : catálogo paginado. Sin texto devuelve todo el catálogo. */
    @Transactional(readOnly = true)
    public PaginaResponse<IngredienteResponse> listar(String texto, Pageable pageable) {
        Pageable limitado = PageRequest.of(pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), TAMANYO_MAXIMO_PAGINA), pageable.getSort());
        String filtro = texto == null ? "" : texto.trim();
        return PaginaResponse.de(ingredienteRepository
                .findByNombreContainingIgnoreCase(filtro, limitado)
                .map(ingredienteMapper::toResponse));
    }
}
