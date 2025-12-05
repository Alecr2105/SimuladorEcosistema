package Business;

import Model.ReporteDatos;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReporteService {

    private static final Logger LOGGER = Logger.getLogger(ReporteService.class.getName());
    private static final String ARCHIVO_ESTADOS = "estado_turnos.txt";
    private static final int TAM_MATRIZ = 10;

    /**
     * Método de conveniencia: siempre devuelve un ReporteDatos,
     * aunque haya problemas leyendo el archivo.
     */
    public ReporteDatos cargarDatosDesdeArchivo() {
        return cargarDatosDesdeArchivo(ARCHIVO_ESTADOS);
    }

    public ReporteDatos cargarDatosDesdeArchivo(String ruta) {
        File archivo = new File(ruta);

        if (!archivo.exists()) {
            LOGGER.log(Level.WARNING, "No se encontró el archivo de estados: {0}", ruta);
            return crearReporteVacio();
        }

        ReporteDatos reporteActual = new ReporteDatos(TAM_MATRIZ * TAM_MATRIZ);
        boolean seLeyeronTurnos = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            Integer turnoEnProceso = null;
            List<String> filas = new ArrayList<>();

            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("---TURNO=")) {
                    // Cerrar turno anterior si estaba en proceso
                    if (turnoEnProceso != null && !filas.isEmpty()) {
                        seLeyeronTurnos |= procesarTurno(turnoEnProceso, filas, reporteActual);
                    }

                    turnoEnProceso = parsearTurnoSeguro(linea);
                    filas = new ArrayList<>();

                    // Si es un nuevo ciclo (turno 0) reiniciamos acumuladores
                    if (turnoEnProceso != null && turnoEnProceso == 0) {
                        reporteActual = new ReporteDatos(TAM_MATRIZ * TAM_MATRIZ);
                    }
                } else if (linea.trim().isEmpty()) {
                    if (turnoEnProceso != null && !filas.isEmpty()) {
                        seLeyeronTurnos |= procesarTurno(turnoEnProceso, filas, reporteActual);
                        turnoEnProceso = null;
                        filas = new ArrayList<>();
                    }
                } else {
                    filas.add(linea.trim());
                }
            }

            // Procesar el último turno que haya quedado pendiente
            if (turnoEnProceso != null && !filas.isEmpty()) {
                seLeyeronTurnos |= procesarTurno(turnoEnProceso, filas, reporteActual);
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error leyendo el archivo de estados: " + ruta, e);
            return crearReporteVacio();
        }

        if (!seLeyeronTurnos) {
            LOGGER.warning("El archivo de estados no contiene turnos válidos de simulación.");
            return crearReporteVacio();
        }

        return reporteActual;
    }

    /**
     * Crea un reporte vacío (todo en 0) para evitar romper la UI.
     */
    private ReporteDatos crearReporteVacio() {
        ReporteDatos datos = new ReporteDatos(TAM_MATRIZ * TAM_MATRIZ);
        datos.setTotalTurnos(0);
        datos.setPresasFinales(0);
        datos.setDepredadoresFinales(0);
        datos.setTerceraEspecieFinal(0);
        datos.setCeldasOcupadas(0);
        datos.setTurnoExtincionPresas(null);
        datos.setTurnoExtincionDepredadores(null);
        return datos;
    }

    /**
     * Procesa un bloque de 10 filas (un turno) y actualiza el reporte.
     */
    private boolean procesarTurno(int turno, List<String> filas, ReporteDatos reporte) {
        if (filas.size() != TAM_MATRIZ) {
            LOGGER.log(Level.WARNING,
                    "El turno {0} no contiene {1} filas completas. Se ignorará este bloque.",
                    new Object[]{turno, TAM_MATRIZ});
            return false;
        }
        int presas = 0;
        int depredadores = 0;
        int terceras = 0;
        int ocupadas = 0;

        for (int indiceFila = 0; indiceFila < filas.size(); indiceFila++) {
            String[] valores = filas.get(indiceFila).split(";");
            if (valores.length != TAM_MATRIZ) {
                LOGGER.log(Level.WARNING,
                        "La fila {0} del turno {1} no tiene {2} columnas. Se ignorará este bloque.",
                        new Object[]{indiceFila + 1, turno, TAM_MATRIZ});
                return false;
            }

            for (String v : valores) {
                int val;

                try {
                    val = Integer.parseInt(v.trim());
                } catch (NumberFormatException ex) {
                    LOGGER.log(Level.WARNING,
                            "Valor no numérico en el turno {0}: ''{1}''. Se ignorará este bloque.",
                            new Object[]{turno, v});
                    return false;
                }

                if (val == 1) {
                    presas++;
                } else if (val == 2) {
                    depredadores++;
                } else if (val == 3) {
                    terceras++;
                }

                if (val != 0) {
                    ocupadas++;
                }
            }
        }

        // Actualizar datos del reporte con el último turno leído
        reporte.setTotalTurnos(Math.max(reporte.getTotalTurnos(), turno));
        reporte.setPresasFinales(presas);
        reporte.setDepredadoresFinales(depredadores);
        reporte.setTerceraEspecieFinal(terceras);
        reporte.setCeldasOcupadas(ocupadas);

        if (reporte.getTurnoExtincionPresas() == null && presas == 0) {
            reporte.setTurnoExtincionPresas(turno);
        }
        if (reporte.getTurnoExtincionDepredadores() == null && depredadores == 0) {
            reporte.setTurnoExtincionDepredadores(turno);
        }
        return true;
    }

    /**
     * Intenta extraer el número de turno de la cabecera, pero nunca lanza excepción.
     */
    private Integer parsearTurnoSeguro(String linea) {
        try {
            int inicio = linea.indexOf("TURNO=");
            int fin = linea.indexOf(";", inicio);
            if (inicio >= 0 && fin > inicio) {
                return Integer.parseInt(linea.substring(inicio + 6, fin));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Cabecera de turno inválida: ''{0}''", linea);
        }
        return null;
    }

    /**
     * Genera un PDF a partir de los datos ya cargados.
     */
    public String generarPdf(ReporteDatos datos, String nombreArchivo) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
        document.open();

        document.add(new Paragraph("Reporte de simulación"));
        document.add(new Paragraph("Turnos ejecutados: " + datos.getTotalTurnos()));
        document.add(new Paragraph("Presas finales: " + datos.getPresasFinales()));
        document.add(new Paragraph("Depredadores finales: " + datos.getDepredadoresFinales()));
        document.add(new Paragraph("Tercera especie final: " + datos.getTerceraEspecieFinal()));
        document.add(new Paragraph("Ocupación: " + datos.getCeldasOcupadas() + "/" + datos.getTotalCeldas()));
        document.add(new Paragraph(String.format("Porcentaje de ocupación: %.2f %%", datos.getPorcentajeOcupacion())));

        String textoExtincionPresas = datos.getTurnoExtincionPresas() == null
                ? "No se extinguieron las presas"
                : "Presas extintas en el turno " + datos.getTurnoExtincionPresas();

        String textoExtincionDepredadores = datos.getTurnoExtincionDepredadores() == null
                ? "No se extinguieron los depredadores"
                : "Depredadores extintos en el turno " + datos.getTurnoExtincionDepredadores();

        document.add(new Paragraph(textoExtincionPresas));
        document.add(new Paragraph(textoExtincionDepredadores));

        document.close();
        return nombreArchivo;
    }

}
