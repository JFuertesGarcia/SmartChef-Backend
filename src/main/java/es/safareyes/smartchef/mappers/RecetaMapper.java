package es.safareyes.smartchef.mappers;

import es.safareyes.smartchef.dtos.RecetaDetalleResponse;
import es.safareyes.smartchef.dtos.RecetaRequest;
import es.safareyes.smartchef.dtos.RecetaResponse;
import es.safareyes.smartchef.modelos.Foto;
import es.safareyes.smartchef.modelos.Ingrediente;
import es.safareyes.smartchef.modelos.Paso;
import es.safareyes.smartchef.modelos.Receta;
import es.safareyes.smartchef.modelos.RecetaIngrediente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecetaMapper {

    private final PasoMapper pasoMapper;
    private final RecetaIngredienteMapper recetaIngredienteMapper;
    private final FotoMapper fotoMapper;

    public Receta toEntity(RecetaRequest request) {
        Receta receta = new Receta();
        updateEntity(receta, request);
        return receta;
    }

    public void updateEntity(Receta receta, RecetaRequest request) {
        receta.setNombre(request.nombre());
        receta.setDescripcion(request.descripcion());
        receta.setRaciones(request.raciones());
        receta.setTiempo(request.tiempo());
        receta.setCalorias(request.calorias());
        receta.setDificultad(request.dificultad());
    }

    public RecetaResponse toResponse(Receta receta) {
        return new RecetaResponse(
                receta.getId(),
                receta.getNombre(),
                receta.getDescripcion(),
                receta.getRaciones(),
                receta.getTiempo(),
                receta.getCalorias(),
                receta.getDificultad()
        );
    }

    /**
     * Receta no tiene colecciones hacia Paso, Foto ni RecetaIngrediente, así que el
     * servicio las consulta y las pasa aquí. El coste (precio x cantidad) y las
     * preferencias (vegetariana / sin gluten) se calculan a partir de los ingredientes.
     */
    public RecetaDetalleResponse toDetalleResponse(Receta receta,
                                                   List<Paso> pasos,
                                                   List<RecetaIngrediente> ingredientes,
                                                   List<Foto> fotos) {
        double coste = 0;
        boolean vegetariana = !ingredientes.isEmpty();
        boolean sinGluten = !ingredientes.isEmpty();
        for (RecetaIngrediente ri : ingredientes) {
            Ingrediente ingrediente = ri.getIngrediente();
            if (ingrediente.getPrecio() != null && ri.getCantidad() != null) {
                coste += ingrediente.getPrecio() * ri.getCantidad();
            }
            vegetariana &= Boolean.TRUE.equals(ingrediente.getVegetariano());
            sinGluten &= Boolean.TRUE.equals(ingrediente.getSinGluten());
        }
        return new RecetaDetalleResponse(
                receta.getId(),
                receta.getNombre(),
                receta.getDescripcion(),
                receta.getRaciones(),
                receta.getTiempo(),
                receta.getCalorias(),
                receta.getDificultad(),
                Math.round(coste * 100.0) / 100.0,
                vegetariana,
                sinGluten,
                pasos.stream().map(pasoMapper::toResponse).toList(),
                ingredientes.stream().map(recetaIngredienteMapper::toResponse).toList(),
                fotos.stream().map(fotoMapper::toResponse).toList()
        );
    }
}
