package es.safareyes.smartchef.repository;

import es.safareyes.smartchef.entity.*;
import es.safareyes.smartchef.repository.projection.CoincidenciaView;
import es.safareyes.smartchef.repository.projection.RecetaTarjetaView;
import es.safareyes.smartchef.repository.spec.RecetaSpecs;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de RecetaRepository contra un PostgreSQL 16 real (Testcontainers).
 * Se usa PostgreSQL y no H2 porque las consultas nativas usan DATE_TRUNC y COUNT(*) FILTER.
 *
 * Datos de prueba (se crean en @BeforeEach; cada test se revierte al terminar):
 *
 *  Ingredientes (por unidad)      veg   sinGluten  kcal  precio
 *    Tomate                       sí    sí         20    0,50
 *    Pasta                        sí    NO         150   0,50
 *    Pollo                        NO    sí         200   1,00
 *
 *  Recetas                        activa portada  min  rac  ingredientes        coste/rac  kcal/rac
 *    R1 Ensalada de tomate        sí     sí       10   1    tomate x2           1,00       40
 *    R2 Pasta con tomate          sí     sí       25   2    tomate x2, pasta x2 1,00       170
 *    R3 Pollo guisado             sí     NO       60   1    pollo x2, tomate x2 3,00       -
 *    R4 Tomate frito retirada     NO     sí       15   1    tomate x2           -          -
 */
@Testcontainers
@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "spring.sql.init.mode=never"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RecetaRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired RecetaRepository recetaRepository;
    @Autowired TestEntityManager em;
    @Autowired EntityManagerFactory emf;

    Usuario chef;
    Usuario cliente;
    Ingrediente tomate, pasta, pollo;
    Receta r1, r2, r3, r4;

    // ------------------------------------------------------------------ fixtures

    @BeforeEach
    void datosDePrueba() {
        chef = crearUsuario("chef@smartchef.es", Rol.CHEF);
        cliente = crearUsuario("julian@smartchef.es", Rol.USUARIO);

        tomate = crearIngrediente("Tomate", true, true, 20, "0.50");
        pasta = crearIngrediente("Pasta", true, false, 150, "0.50");
        pollo = crearIngrediente("Pollo", false, true, 200, "1.00");

        r1 = crearReceta("Ensalada de tomate", 10, 1, true);
        linea(r1, tomate, 2);
        paso(r1, 1, "Trocear el tomate");
        paso(r1, 2, "Aliñar");
        portada(r1);

        r2 = crearReceta("Pasta con tomate", 25, 2, true);
        linea(r2, tomate, 2);
        linea(r2, pasta, 2);
        paso(r2, 1, "Cocer la pasta");
        portada(r2);

        r3 = crearReceta("Pollo guisado", 60, 1, true);   // sin portada
        linea(r3, pollo, 2);
        linea(r3, tomate, 2);

        r4 = crearReceta("Tomate frito retirada", 15, 1, false);
        linea(r4, tomate, 2);
        portada(r4);

        em.flush();
        em.clear();   // fuerza a releer de BD en cada test
    }

    private Usuario crearUsuario(String email, Rol rol) {
        // Ajusta los campos a tu entidad Usuario
        return em.persist(Usuario.builder().email(email).nombre(email).contrasenya("x").rol(rol).build());
    }

    private Ingrediente crearIngrediente(String nombre, boolean veg, boolean sinGluten, int kcal, String precio) {
        return em.persist(Ingrediente.builder()
                .nombre(nombre).vegetariano(veg).sinGluten(sinGluten)
                .calorias(kcal).precio(new BigDecimal(precio)).build());
    }

    private Receta crearReceta(String nombre, int tiempo, int raciones, boolean activa) {
        return em.persist(Receta.builder()
                .nombre(nombre).descripcion("Descripción de " + nombre)
                .tiempo(tiempo).raciones(raciones).dificultad(Dificultad.FACIL)
                .activa(activa).autor(chef).build());
    }

    private void linea(Receta r, Ingrediente i, int cantidad) {
        em.persist(RecetaIngrediente.builder().receta(r).ingrediente(i).cantidad(cantidad).build());
    }

    private void paso(Receta r, int numero, String texto) {
        em.persist(Paso.builder().receta(r).numero(numero).descripcion(texto).build());
    }

    private void portada(Receta r) {
        // Ajusta los campos obligatorios a tu entidad FotoReceta
        em.persist(FotoReceta.builder().receta(r).portada(true).orden(1)
                .claveDisco(java.util.UUID.randomUUID() + ".jpg")
                .nombreOriginal("foto.jpg").tipo("image/jpeg").tamano(1000L).build());
    }

    private void despensa(Usuario u, Ingrediente i, LocalDate caducidad) {
        em.persist(Despensa.builder().usuario(u).ingrediente(i).cantidad(5).caducidad(caducidad).build());
        em.flush();
        em.clear();
    }

    private static List<String> nombres(Iterable<Receta> recetas) {
        return java.util.stream.StreamSupport.stream(recetas.spliterator(), false)
                .map(Receta::getNombre).toList();
    }

    // ====================================================================== Q1

    @Nested
    @DisplayName("Q1 · Métodos derivados")
    class Q1 {

        @Test
        void buscaPorNombreIgnorandoMayusculasYExcluyendoRetiradas() {
            Page<Receta> page = recetaRepository
                    .findByNombreContainingIgnoreCaseAndActivaTrue("TOMATE", Pageable.unpaged());

            assertThat(nombres(page)).containsExactlyInAnyOrder("Ensalada de tomate", "Pasta con tomate");
        }

        @Test
        void listaRecetasActivasDeUnChefOrdenadasPorNombre() {
            List<Receta> recetas = recetaRepository.findByAutorIdAndActivaTrueOrderByNombreAsc(chef.getId());

            assertThat(nombres(recetas)).containsExactly(
                    "Ensalada de tomate", "Pasta con tomate", "Pollo guisado");
        }

        @Test
        void filtraPorRangoDeTiempoOrdenadoAscendente() {
            List<Receta> recetas = recetaRepository.findByActivaTrueAndTiempoBetweenOrderByTiempoAsc(10, 30);

            assertThat(nombres(recetas)).containsExactly("Ensalada de tomate", "Pasta con tomate");
        }

        @Test
        void findByIdAndActivaTrueNoDevuelveRecetasRetiradas() {
            assertThat(recetaRepository.findByIdAndActivaTrue(r1.getId())).isPresent();
            assertThat(recetaRepository.findByIdAndActivaTrue(r4.getId())).isEmpty();
        }

        @Test
        void detectaNombreDuplicadoDelMismoAutorIgnorandoMayusculas() {
            assertThat(recetaRepository.existsByNombreIgnoreCaseAndAutorId("PASTA CON TOMATE", chef.getId())).isTrue();
            assertThat(recetaRepository.existsByNombreIgnoreCaseAndAutorId("Pasta con tomate", cliente.getId())).isFalse();
            assertThat(recetaRepository.existsByNombreIgnoreCaseAndAutorId("Inexistente", chef.getId())).isFalse();
        }

        @Test
        void cuentaSoloLasRecetasActivasDelAutor() {
            assertThat(recetaRepository.countByAutorIdAndActivaTrue(chef.getId())).isEqualTo(3);
            assertThat(recetaRepository.countByAutorIdAndActivaTrue(cliente.getId())).isZero();
        }
    }

    // ====================================================================== Q2

    @Nested
    @DisplayName("Q2 · JPQL")
    class Q2 {

        @Test
        void calculaElCostePorRacion() {
            assertThat(recetaRepository.costePorRacion(r1.getId()).orElseThrow()).isEqualByComparingTo("1.00");
            assertThat(recetaRepository.costePorRacion(r2.getId()).orElseThrow()).isEqualByComparingTo("1.00");
            assertThat(recetaRepository.costePorRacion(r3.getId()).orElseThrow()).isEqualByComparingTo("3.00");
        }

        @Test
        void costePorRacionDeRecetaSinIngredientesEsVacio() {
            Receta vacia = em.persist(Receta.builder().nombre("Vacía").tiempo(5).raciones(1)
                    .dificultad(Dificultad.FACIL).activa(true).autor(chef).build());

            assertThat(recetaRepository.costePorRacion(vacia.getId())).isEmpty();
        }

        @Test
        void calculaLasCaloriasPorRacion() {
            assertThat(recetaRepository.caloriasPorRacion(r1.getId())).contains(40L);          // 2*20 / 1
            assertThat(recetaRepository.caloriasPorRacion(r2.getId())).contains(170L);         // (2*20 + 2*150) / 2
        }

        @Test
        void esVegetarianaSoloSiTodosLosIngredientesLoSon() {
            assertThat(recetaRepository.esVegetariana(r1.getId())).isTrue();
            assertThat(recetaRepository.esVegetariana(r2.getId())).isTrue();
            assertThat(recetaRepository.esVegetariana(r3.getId())).isFalse();   // lleva pollo
        }

        @Test
        void esSinGlutenSoloSiTodosLosIngredientesLoSon() {
            assertThat(recetaRepository.esSinGluten(r1.getId())).isTrue();
            assertThat(recetaRepository.esSinGluten(r2.getId())).isFalse();     // lleva pasta
            assertThat(recetaRepository.esSinGluten(r3.getId())).isTrue();
        }

        @Test
        void findEconomicasDevuelveActivasConCosteIgualOInferiorAlMaximo() {
            List<Receta> economicas = recetaRepository.findEconomicas(new BigDecimal("2.50"));

            // R3 cuesta 3,00 y R4 está retirada
            assertThat(nombres(economicas)).containsExactlyInAnyOrder("Ensalada de tomate", "Pasta con tomate");
        }

        @Test
        void findEconomicasIncluyeElLimiteExacto() {
            List<Receta> economicas = recetaRepository.findEconomicas(new BigDecimal("1.00"));

            assertThat(nombres(economicas)).containsExactlyInAnyOrder("Ensalada de tomate", "Pasta con tomate");
        }

        @Test
        void findConIngredienteSoloDevuelveRecetasActivasQueLoUsan() {
            List<Receta> conTomate = recetaRepository.findConIngrediente(tomate.getId(), Pageable.unpaged());
            List<Receta> conPollo = recetaRepository.findConIngrediente(pollo.getId(), Pageable.unpaged());

            assertThat(nombres(conTomate)).containsExactly(
                    "Ensalada de tomate", "Pasta con tomate", "Pollo guisado");   // ordenadas por tiempo, sin R4
            assertThat(nombres(conPollo)).containsExactly("Pollo guisado");
        }

        @Test
        void findConIngredienteRespetaElLimiteDePagina() {
            List<Receta> una = recetaRepository.findConIngrediente(tomate.getId(), PageRequest.of(0, 1));

            assertThat(nombres(una)).containsExactly("Ensalada de tomate");
        }

        @Test
        void ingredientesFaltantesExcluyeLoQueHayEnDespensa() {
            despensa(cliente, tomate, LocalDate.now().plusDays(5));

            List<String> faltan = recetaRepository.ingredientesFaltantes(r2.getId(), cliente.getId());

            assertThat(faltan).containsExactly("Pasta");
        }

        @Test
        void ingredientesFaltantesTrataComoFaltanteLoCaducado() {
            despensa(cliente, tomate, LocalDate.now().minusDays(1));   // caducado

            List<String> faltan = recetaRepository.ingredientesFaltantes(r1.getId(), cliente.getId());

            assertThat(faltan).containsExactly("Tomate");
        }

        @Test
        void ingredientesFaltantesConDespensaVaciaDevuelveTodos() {
            assertThat(recetaRepository.ingredientesFaltantes(r2.getId(), cliente.getId()))
                    .containsExactly("Pasta", "Tomate");   // ordenados por nombre
        }
    }

    // ====================================================================== Q3

    @Nested
    @DisplayName("Q3 · SQL nativo (PostgreSQL)")
    class Q3 {

        @Test
        void buscarPorDespensaDevuelveRecetasConMasDelCincuentaPorCiento() {
            despensa(cliente, tomate, null);                        // null = no caduca
            // pasta no está en la despensa

            List<CoincidenciaView> res = recetaRepository.buscarPorDespensa(cliente.getId());

            // R1 100 %. R2 y R3 tienen un 50 %, que NO es "más del 50 %". R4 está retirada.
            assertThat(res).extracting(CoincidenciaView::getRecetaId).containsExactly(r1.getId());
            assertThat(res.get(0).getPorcentaje()).isEqualTo(100.0);
        }

        @Test
        void buscarPorDespensaOrdenaPorPorcentajeDescendente() {
            despensa(cliente, tomate, null);
            despensa(cliente, pasta, null);
            despensa(cliente, pollo, null);

            List<CoincidenciaView> res = recetaRepository.buscarPorDespensa(cliente.getId());

            assertThat(res).extracting(CoincidenciaView::getRecetaId)
                    .containsExactlyInAnyOrder(r1.getId(), r2.getId(), r3.getId());
            assertThat(res).allSatisfy(c -> assertThat(c.getPorcentaje()).isEqualTo(100.0));
        }

        @Test
        void buscarPorDespensaIgnoraLosIngredientesCaducados() {
            despensa(cliente, tomate, null);
            despensa(cliente, pasta, LocalDate.now().minusDays(1));   // caducada

            List<CoincidenciaView> res = recetaRepository.buscarPorDespensa(cliente.getId());

            // R2 baja de 100 % a 50 % y desaparece
            assertThat(res).extracting(CoincidenciaView::getRecetaId).containsExactly(r1.getId());
        }

        @Test
        void buscarPorDespensaNoMezclaDespensasDeOtrosUsuarios() {
            despensa(cliente, tomate, null);

            assertThat(recetaRepository.buscarPorDespensa(chef.getId())).isEmpty();
        }

        @Test
        void buscarPorDespensaNoDevuelveRecetasRetiradas() {
            despensa(cliente, tomate, null);

            assertThat(recetaRepository.buscarPorDespensa(cliente.getId()))
                    .extracting(CoincidenciaView::getRecetaId)
                    .doesNotContain(r4.getId());
        }

        @Test
        void buscarPorIngredientesUsaLaListaRecibida() {
            List<CoincidenciaView> res = recetaRepository
                    .buscarPorIngredientes(Set.of(tomate.getId(), pasta.getId()));

            // R1 100 %, R2 100 %; R3 solo coincide en tomate (50 %, no es "más del 50 %")
            assertThat(res).extracting(CoincidenciaView::getRecetaId)
                    .containsExactlyInAnyOrder(r1.getId(), r2.getId());
        }

        @Test
        void buscarPorIngredientesExigeMasDelCincuentaPorCiento() {
            List<CoincidenciaView> res = recetaRepository.buscarPorIngredientes(Set.of(tomate.getId()));

            // R1 100 %; R2 y R3 50 % (excluidas)
            assertThat(res).extracting(CoincidenciaView::getRecetaId).containsExactly(r1.getId());
        }
    }

    // ====================================================================== Q5 + Q6

    @Nested
    @DisplayName("Q5/Q6 · Proyección por interfaz y paginación")
    class Q5Q6 {

        @Test
        void findTarjetasSoloMuestraActivasConPortada() {
            Page<RecetaTarjetaView> page = recetaRepository.findTarjetas(PageRequest.of(0, 10, Sort.by("nombre")));

            assertThat(page.getTotalElements()).isEqualTo(2);
            assertThat(page.getContent()).extracting(RecetaTarjetaView::getNombre)
                    .containsExactly("Ensalada de tomate", "Pasta con tomate");   // R3 sin portada, R4 retirada
        }

        @Test
        void findTarjetasExponeLosCamposDeLaProyeccion() {
            RecetaTarjetaView tarjeta = recetaRepository
                    .findTarjetas(PageRequest.of(0, 1, Sort.by("nombre"))).getContent().get(0);

            assertThat(tarjeta.getId()).isEqualTo(r1.getId());
            assertThat(tarjeta.getTiempo()).isEqualTo(10);
            assertThat(tarjeta.getRaciones()).isEqualTo(1);
            assertThat(tarjeta.getDificultad()).isEqualTo("FACIL");
        }

        @Test
        void paginaCorrectamenteYCuentaElTotal() {
            Page<RecetaTarjetaView> primera = recetaRepository.findTarjetas(PageRequest.of(0, 1, Sort.by("nombre")));
            Page<RecetaTarjetaView> segunda = recetaRepository.findTarjetas(PageRequest.of(1, 1, Sort.by("nombre")));

            assertThat(primera.getTotalPages()).isEqualTo(2);
            assertThat(primera.getContent()).hasSize(1);
            assertThat(segunda.getContent().get(0).getNombre()).isEqualTo("Pasta con tomate");
            assertThat(segunda.isLast()).isTrue();
        }

        @Test
        void ordenaDescendentePorTiempo() {
            Page<RecetaTarjetaView> page = recetaRepository
                    .findTarjetas(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "tiempo")));

            assertThat(page.getContent()).extracting(RecetaTarjetaView::getNombre)
                    .containsExactly("Pasta con tomate", "Ensalada de tomate");
        }
    }

    // ====================================================================== Q7

    @Nested
    @DisplayName("Q7 · Specifications")
    class Q7 {

        private Specification<Receta> base() {
            return Specification.where(RecetaSpecs.activa()).and(RecetaSpecs.conPortada());
        }

        @Test
        void sinFiltrosOpcionalesDevuelveElCatalogoVisible() {
            List<Receta> res = recetaRepository.findAll(base()
                    .and(RecetaSpecs.nombreContiene(null))
                    .and(RecetaSpecs.vegetariana(null))
                    .and(RecetaSpecs.dificultad(null)));

            assertThat(nombres(res)).containsExactlyInAnyOrder("Ensalada de tomate", "Pasta con tomate");
        }

        @Test
        void activaExcluyeRetiradasYConPortadaExcluyeLasQueNoLaTienen() {
            assertThat(nombres(recetaRepository.findAll(Specification.where(RecetaSpecs.activa()))))
                    .containsExactlyInAnyOrder("Ensalada de tomate", "Pasta con tomate", "Pollo guisado");
            assertThat(nombres(recetaRepository.findAll(Specification.where(RecetaSpecs.conPortada()))))
                    .containsExactlyInAnyOrder("Ensalada de tomate", "Pasta con tomate", "Tomate frito retirada");
        }

        @Test
        void filtraPorTextoSinImportarMayusculas() {
            assertThat(nombres(recetaRepository.findAll(base().and(RecetaSpecs.nombreContiene("  PASTA ")))))
                    .containsExactly("Pasta con tomate");
        }

        @Test
        void filtraVegetarianas() {
            Specification<Receta> spec = Specification.where(RecetaSpecs.activa()).and(RecetaSpecs.vegetariana(true));

            assertThat(nombres(recetaRepository.findAll(spec)))
                    .containsExactlyInAnyOrder("Ensalada de tomate", "Pasta con tomate");   // R3 lleva pollo
        }

        @Test
        void filtraSinGluten() {
            Specification<Receta> spec = Specification.where(RecetaSpecs.activa()).and(RecetaSpecs.sinGluten(true));

            assertThat(nombres(recetaRepository.findAll(spec)))
                    .containsExactlyInAnyOrder("Ensalada de tomate", "Pollo guisado");   // R2 lleva pasta
        }

        @Test
        void filtraRapidasConLimiteDeTreintaMinutos() {
            Specification<Receta> spec = Specification.where(RecetaSpecs.activa()).and(RecetaSpecs.rapida(true));

            assertThat(nombres(recetaRepository.findAll(spec)))
                    .containsExactlyInAnyOrder("Ensalada de tomate", "Pasta con tomate");   // R3 tarda 60
        }

        @Test
        void filtraEconomicasConLimiteDeDosConCincuentaPorRacion() {
            Specification<Receta> spec = Specification.where(RecetaSpecs.activa()).and(RecetaSpecs.economica(true));

            assertThat(nombres(recetaRepository.findAll(spec)))
                    .containsExactlyInAnyOrder("Ensalada de tomate", "Pasta con tomate");   // R3 cuesta 3,00
        }

        @Test
        void filtroFalseSeIgnoraIgualQueNull() {
            Specification<Receta> spec = Specification.where(RecetaSpecs.activa())
                    .and(RecetaSpecs.vegetariana(false))
                    .and(RecetaSpecs.rapida(false));

            assertThat(nombres(recetaRepository.findAll(spec))).hasSize(3);
        }

        @Test
        void filtraPorTiempoMaximoYDificultad() {
            Specification<Receta> spec = base()
                    .and(RecetaSpecs.tiempoMaximo(20))
                    .and(RecetaSpecs.dificultad(Dificultad.FACIL));

            assertThat(nombres(recetaRepository.findAll(spec))).containsExactly("Ensalada de tomate");
        }

        @Test
        void combinaVariosFiltrosConAnd() {
            Specification<Receta> spec = base()
                    .and(RecetaSpecs.vegetariana(true))
                    .and(RecetaSpecs.sinGluten(true))
                    .and(RecetaSpecs.rapida(true))
                    .and(RecetaSpecs.economica(true));

            // Solo R1 cumple las cuatro; R2 lleva pasta (gluten)
            assertThat(nombres(recetaRepository.findAll(spec))).containsExactly("Ensalada de tomate");
        }

        @Test
        void combinacionSinResultadosDevuelveListaVacia() {
            Specification<Receta> spec = base().and(RecetaSpecs.sinGluten(true)).and(RecetaSpecs.tiempoMaximo(5));

            assertThat(recetaRepository.findAll(spec)).isEmpty();
        }

        @Test
        void funcionaConPaginacionYOrdenacion() {
            Page<Receta> page = recetaRepository.findAll(base().and(RecetaSpecs.vegetariana(true)),
                    PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "nombre")));

            assertThat(page.getTotalElements()).isEqualTo(2);
            assertThat(page.getContent().get(0).getNombre()).isEqualTo("Pasta con tomate");
        }
    }

    // ====================================================================== Q8

    @Nested
    @DisplayName("Q8 · @EntityGraph (N+1)")
    class Q8 {

        @Test
        void cargaRecetaConIngredientesYPasosEnUnaSolaConsulta() {
            Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
            stats.clear();

            Receta receta = recetaRepository.findDetalleById(r1.getId()).orElseThrow();
            // Acceder a las colecciones y a cada ingrediente NO debe lanzar consultas extra
            int ingredientes = receta.getIngredientes().size();
            receta.getIngredientes().forEach(ri -> ri.getIngrediente().getNombre());
            int pasos = receta.getPasos().size();

            assertThat(ingredientes).isEqualTo(1);
            assertThat(pasos).isEqualTo(2);
            // Requiere que Receta.autor sea LAZY (si es EAGER añade una consulta más)
            assertThat(stats.getPrepareStatementCount()).isEqualTo(1);
        }

        @Test
        void losPasosLlegaranEnOrdenSiLaEntidadLoDeclara() {
            Receta receta = recetaRepository.findDetalleById(r1.getId()).orElseThrow();

            // Añade @OrderBy("numero ASC") en Receta.pasos para que esto se cumpla siempre
            assertThat(receta.getPasos()).extracting(Paso::getNumero).containsExactly(1, 2);
        }

        @Test
        void detalleDeRecetaInexistenteEsVacio() {
            assertThat(recetaRepository.findDetalleById(-1L)).isEmpty();
        }
    }
}
