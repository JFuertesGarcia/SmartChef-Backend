package es.safareyes.smartchef.servicios;

import es.safareyes.smartchef.constantes.TipoRegistro;
import es.safareyes.smartchef.dtos.DiaRegistroResponse;
import es.safareyes.smartchef.dtos.RecetaUsuarioRequest;
import es.safareyes.smartchef.dtos.RecetaUsuarioResponse;
import es.safareyes.smartchef.dtos.RegistroItemResponse;
import es.safareyes.smartchef.dtos.RegistroRequest;
import es.safareyes.smartchef.dtos.SemanaResponse;
import es.safareyes.smartchef.excepciones.RecursoNoEncontradoException;
import es.safareyes.smartchef.excepciones.ReglaNegocioException;
import es.safareyes.smartchef.mappers.RecetaMapper;
import es.safareyes.smartchef.mappers.RecetaUsuarioMapper;
import es.safareyes.smartchef.modelos.Receta;
import es.safareyes.smartchef.modelos.RecetaUsuario;
import es.safareyes.smartchef.modelos.Usuario;
import es.safareyes.smartchef.repositorios.RecetaRepository;
import es.safareyes.smartchef.repositorios.RecetaUsuarioRepository;
import es.safareyes.smartchef.seguridad.ControlAcceso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistroService {

    private final RecetaRepository recetaRepository;
    private final RecetaUsuarioRepository recetaUsuarioRepository;
    private final RecetaUsuarioMapper recetaUsuarioMapper;
    private final RecetaMapper recetaMapper;
    private final ControlAcceso controlAcceso;

    /** POST /registros (RN-07): registra que el usuario ha cocinado una receta. */
    @Transactional
    public RecetaUsuarioResponse registrar(Integer usuarioId, RegistroRequest request) {
        Usuario usuario = controlAcceso.usuario(usuarioId);
        Receta receta = recetaRepository.findByIdAndActivaTrue(request.idReceta())
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada: " + request.idReceta()));

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fecha = request.fecha() != null ? request.fecha() : ahora;
        if (fecha.isAfter(ahora)) {
            throw new ReglaNegocioException("No puedes registrar como cocinada una receta en el futuro");
        }

        RecetaUsuario registro = recetaUsuarioMapper.toEntity(
                new RecetaUsuarioRequest(receta.getId(), usuarioId, TipoRegistro.COCINADA), receta, usuario);
        registro.setFecha(fecha);
        return recetaUsuarioMapper.toResponse(recetaUsuarioRepository.save(registro));
    }

    /** GET /registros/semana?fecha= : la semana (lunes a domingo) de esa fecha, por días y con calorías. */
    @Transactional(readOnly = true)
    public SemanaResponse semana(Integer usuarioId, LocalDate fecha) {
        controlAcceso.usuario(usuarioId);

        LocalDate referencia = fecha != null ? fecha : LocalDate.now();
        LocalDate lunes = referencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = lunes.plusDays(6);

        List<RecetaUsuario> registros = recetaUsuarioRepository.findDelUsuarioEntre(
                usuarioId, TipoRegistro.COCINADA,
                lunes.atStartOfDay(), domingo.plusDays(1).atStartOfDay());
        Map<LocalDate, List<RecetaUsuario>> porDia = registros.stream()
                .collect(Collectors.groupingBy(r -> r.getFecha().toLocalDate()));

        List<DiaRegistroResponse> dias = lunes.datesUntil(domingo.plusDays(1))
                .map(dia -> {
                    List<RegistroItemResponse> items = porDia.getOrDefault(dia, List.of()).stream()
                            .map(r -> new RegistroItemResponse(
                                    r.getId(), r.getFecha(), recetaMapper.toResponse(r.getReceta())))
                            .toList();
                    int calorias = items.stream()
                            .mapToInt(i -> i.receta().calorias() != null ? i.receta().calorias() : 0)
                            .sum();
                    return new DiaRegistroResponse(dia, items, calorias);
                })
                .toList();

        int total = dias.stream().mapToInt(DiaRegistroResponse::calorias).sum();
        return new SemanaResponse(lunes, domingo, dias, total);
    }
}
