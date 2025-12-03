package Data;

import Model.Ecosistema;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EcosistemaDAO {

    private static final String ARCHIVO_INICIAL = "ecosistema.txt";
    private static final String ARCHIVO_ESTADOS = "estado_turnos.txt";

    // Guarda datos iniciales + matriz inicial
    public void guardarDatosIniciales(Ecosistema ecosistema,
                                      int cantPresas,
                                      int cantDepredadores,
                                      int maxTurnos,
                                      String escenario) {

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

    // Guarda el estado de un turno (se va acumulando en el archivo)
    public void guardarEstadoTurno(Ecosistema ecosistema,
                                   int numeroTurno,
                                   String escenario) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_ESTADOS, true))) {

            // Encabezado del turno
            bw.write("---TURNO=" + numeroTurno + ";ESCENARIO=" + escenario + "---");
            bw.newLine();

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

            bw.newLine(); // línea en blanco entre turnos

        } catch (IOException e) {
            System.out.println("Error al guardar estado_turnos.txt: " + e.getMessage());
        }
    }
}

