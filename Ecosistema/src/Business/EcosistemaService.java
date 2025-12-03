package Business;

import Data.EcosistemaDAO;
import Model.Ecosistema;

public class EcosistemaService {

    private Ecosistema ecosistema;
    private EcosistemaDAO ecosistemaDAO;

    // NUEVOS servicios especializados
    private MovimientoService movimientoService;
    private AlimentacionService alimentacionService;
    private ReproduccionService reproduccionService;

    public EcosistemaService() {
        ecosistema = new Ecosistema();
        ecosistemaDAO = new EcosistemaDAO();

        movimientoService = new MovimientoService();
        alimentacionService = new AlimentacionService();
        reproduccionService = new ReproduccionService();
    }

    public void generarEscenario(int presas, int depredadores) {
        ecosistema.generarEscenario(presas, depredadores);
    }

    public int[][] getMatrizNumerica() {
        return ecosistema.getMatrizNumerica();
    }

    public Ecosistema getEcosistema() {
        return ecosistema;
    }

    // ---------- MOVIMIENTO (usa MovimientoService) ----------

    public void moverDepredadores() {
        movimientoService.moverDepredadores(ecosistema);
    }

    public void moverPresas() {
        movimientoService.moverPresas(ecosistema);
    }

    // ---------- FIN DE TURNO: hambre + reproducción ----------

    public void aplicarFinDeTurnoBasico() {
        // hambre, contadores, muerte y reproducción se manejan aquí
        alimentacionService.aplicarReglasAlimentacionYFinTurno(ecosistema);

        // "gancho" para futura separación de reproducción, ahora es un no-op
        reproduccionService.aplicarReglasReproduccion(ecosistema);
    }

    // ---------- ARCHIVOS ----------

    public void guardarDatosIniciales(int presas,
                                      int depredadores,
                                      int maxTurnos,
                                      String escenario) {

        ecosistemaDAO.guardarDatosIniciales(ecosistema, presas, depredadores, maxTurnos, escenario);
    }

    public void guardarEstadoTurno(int numeroTurno, String escenario) {
        ecosistemaDAO.guardarEstadoTurno(ecosistema, numeroTurno, escenario);
    }
}

