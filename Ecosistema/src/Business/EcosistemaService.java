package Business;

import Data.EcosistemaDAO;
import Model.Ecosistema;
import java.util.List;

public class EcosistemaService {
    private Ecosistema ecosistema;
    private EcosistemaDAO ecosistemaDAO;
    
    public EcosistemaService() {
        ecosistema = new Ecosistema();
        ecosistemaDAO = new EcosistemaDAO();
    }

    
    
    //MÉTODOS PRINCIPALES:
    //******************************************************************************
    //Generamos los escenarios:
    public void generarEscenario(int presas,
            int depredadores,
            int terceras,
            String varianteTercera,
            boolean mutacionesActivas,
            String tipoMutacion) {
        ecosistema.generarEscenario(presas, depredadores, terceras, varianteTercera,
                mutacionesActivas, tipoMutacion);
    }
    

    //Acceso al modelo:
    public int[][] getMatrizNumerica() {
        return ecosistema.getMatrizNumerica();
    }

    public Ecosistema getEcosistema() {
        return ecosistema;
    }
    
    
    //Movimientos (delegamos desde ecosistem.java):
    public void moverDepredadores() {
        ecosistema.moverSoloDepredadores(ecosistema);
    }

    public void moverPresas() {
        ecosistema.moverSoloPresas(ecosistema);
    }

    public void moverTerceraEspecie() {
        ecosistema.moverSoloTerceraEspecie();
    }

    
    
    
    //FIN DE TURNO: hambre + reproducción.
    public void aplicarFinDeTurnoBasico() {
        // hambre, contadores, muerte y reproducción
        ecosistema.aplicarFinDeTurnoBasico(ecosistema);
    }
    

    //Pasamos datos iniciales a ecosistemasDAO para guardar en el archivo ecosistemas.txt:
    public void guardarDatosIniciales(int presas,
            int depredadores,
            int maxTurnos,
            String escenario) {
        ecosistemaDAO.guardarDatosIniciales(ecosistema, presas, depredadores, maxTurnos, escenario);
    }

    
    public void guardarEstadoTurno(int numeroTurno, String escenario, List<String> eventosTurno) {
        ecosistemaDAO.guardarEstadoTurno(ecosistema, numeroTurno, escenario, eventosTurno);
    }


    
    
    
    //Getters propios de la clase EcosistemasService:
    //***********************************************
    public int getTotalPresas() {
        return ecosistema.contarPresas();
    }

    public int getTotalDepredadores() {
        return ecosistema.contarDepredadores();
    }

    public int getTotalTerceraEspecie() {
        return ecosistema.contarTerceraEspecie();
    }

    public java.util.List<String> consumirEventosTurno() {
        return ecosistema.consumirEventosTurno();
    }
}
