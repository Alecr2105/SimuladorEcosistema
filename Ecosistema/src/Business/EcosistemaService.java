package Business;

import Data.EcosistemaDAO;
import Model.Ecosistema;

public class EcosistemaService {

    private Ecosistema ecosistema;
    private EcosistemaDAO ecosistemaDAO;

    // Servicios especializados
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

    
    
    
    //Generamos los escenarios:
    //**********************************************************
    public void generarEscenario(int presas, int depredadores) {
        ecosistema.generarEscenario(presas, depredadores);
    }

    // Versión con tercera especie
    public void generarEscenario(int presas,
                                 int depredadores,
                                 int terceras,
                                 String varianteTercera) {
        ecosistema.generarEscenario(presas, depredadores, terceras, varianteTercera);
    }

    // ========== ACCESO AL MODELO ==========

    public int[][] getMatrizNumerica() {
        return ecosistema.getMatrizNumerica();
    }

    public Ecosistema getEcosistema() {
        return ecosistema;
    }

    // ========== MOVIMIENTO (usa MovimientoService / modelo) ==========

    public void moverDepredadores() {
        // delegamos en MovimientoService
        movimientoService.moverDepredadores(ecosistema);
    }

    public void moverPresas() {
        // delegamos en MovimientoService
        movimientoService.moverPresas(ecosistema);
    }

    // 👉 ESTE ES EL QUE TE FALTABA
    public void moverTerceraEspecie() {
        // por ahora llamamos directo al modelo
        ecosistema.moverSoloTerceraEspecie();
        // si quieres, luego puedes mover esto a MovimientoService
    }

    // ========== FIN DE TURNO: hambre + reproducción ==========

    public void aplicarFinDeTurnoBasico() {
        // hambre, contadores, muerte y reproducción
        alimentacionService.aplicarReglasAlimentacionYFinTurno(ecosistema);

        // gancho para futura lógica extra de reproducción
        reproduccionService.aplicarReglasReproduccion(ecosistema);
    }

    // ========== ARCHIVOS ==========

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
