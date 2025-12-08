package Data;

import Model.Ecosistema;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;





public class EcosistemaDAO {
    private static final String ARCHIVO_INICIAL = "ecosistema.txt";
    private static final String ARCHIVO_ESTADOS = "estado_turnos.txt";

    // Guarda datos iniciales + matriz inicial
    public void guardarDatosIniciales(Ecosistema ecosistema,
                                      int cantPresas,
                                      int cantDepredadores,
                                      int maxTurnos,
                                      String escenario) {
        
        reiniciarArchivoEstados();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_INICIAL, false))) {

            // Primera línea: metadatos del escenario
            bw.write("ESCENARIO=" + escenario
                    + ";PRESAS=" + cantPresas
                    + ";DEPREDADORES=" + cantDepredadores
                    + ";MAX_TURNOS=" + maxTurnos);
            bw.newLine();

            // Matriz inicial (10x10) en forma numérica
            int[][] m = ecosistema.getMatrizNumerica();
            for (int i = 0; i < m.length; i++) {
                StringBuilder fila = new StringBuilder();
                for (int j = 0; j < m[i].length; j++) {
                    fila.append(m[i][j]);
                    if (j < m[i].length - 1) {
                        fila.append(";");
                    }
                }
                bw.write(fila.toString());
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error al guardar ecosistema.txt: " + e.getMessage());
        }
    }
    
    /**
     * El archivo de estados se usa para que el módulo de reportes lea el último
     * ciclo de simulación. Antes de iniciar una nueva corrida lo truncamos para
     * evitar que queden turnos viejos mezclados con los actuales y que el
     * parser del reporte encuentre datos corruptos.
     */
    private void reiniciarArchivoEstados() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_ESTADOS, false))) {
            // El try-with-resources crea/trunca el archivo, no necesitamos escribir nada.
        } catch (IOException e) {
            System.out.println("No se pudo reiniciar estado_turnos.txt: " + e.getMessage());
        }
    }

    // Guarda el estado de un turno (se va acumulando en el archivo)
    public void guardarEstadoTurno(Ecosistema ecosistema,
                                   int numeroTurno,
                                   String escenario,
                                   List<String> eventosTurno) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_ESTADOS, true))) {

            // Encabezado del turno
            bw.write("---TURNO=" + numeroTurno + ";ESCENARIO=" + escenario + "---");
            bw.newLine();

            if (eventosTurno == null || eventosTurno.isEmpty()) {
                bw.write("Sin eventos registrados en este turno.");
                bw.newLine();
            } else {
                for (String evento : eventosTurno) {
                    bw.write(evento);
                    bw.newLine();
                }
            }

            bw.newLine(); // línea en blanco entre turnos

        } catch (IOException e) {
            System.out.println("Error al guardar estado_turnos.txt: " + e.getMessage());
        }
    }
}

