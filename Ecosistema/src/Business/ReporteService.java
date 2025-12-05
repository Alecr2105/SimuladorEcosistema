package Business;
import Model.ReporteDatos;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class ReporteService {
    
    private static final String ARCHIVO_ESTADOS = "estado_turnos.txt";
    private static final int TAM_MATRIZ = 10;

    public ReporteDatos cargarDatosDesdeArchivo() throws IOException {
        return cargarDatosDesdeArchivo(ARCHIVO_ESTADOS);
    }

    public ReporteDatos cargarDatosDesdeArchivo(String ruta) throws IOException {
        ReporteDatos ultimoReporte = new ReporteDatos(TAM_MATRIZ * TAM_MATRIZ);
        ReporteDatos reporteActual = new ReporteDatos(TAM_MATRIZ * TAM_MATRIZ);

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            Integer turnoEnProceso = null;
            List<String> filas = new ArrayList<>();

            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("---TURNO=")) {
                    if (turnoEnProceso != null && !filas.isEmpty()) {
                        procesarTurno(turnoEnProceso, filas, reporteActual);
                    }

                    turnoEnProceso = parsearTurno(linea);
                    filas = new ArrayList<>();

                    // Si es un nuevo ciclo (turno 0) reiniciamos acumuladores
                    if (turnoEnProceso == 0) {
                        reporteActual = new ReporteDatos(TAM_MATRIZ * TAM_MATRIZ);
                    }
                } else if (linea.trim().isEmpty()) {
                    if (turnoEnProceso != null && !filas.isEmpty()) {
                        procesarTurno(turnoEnProceso, filas, reporteActual);
                        turnoEnProceso = null;
                        filas = new ArrayList<>();
                    }
                } else {
                    filas.add(linea.trim());
                }
            }

            if (turnoEnProceso != null && !filas.isEmpty()) {
                procesarTurno(turnoEnProceso, filas, reporteActual);
            }

            ultimoReporte = reporteActual;
        }

        return ultimoReporte;
    }

    private void procesarTurno(int turno, List<String> filas, ReporteDatos reporte) {
        int presas = 0;
        int depredadores = 0;
        int terceras = 0;
        int ocupadas = 0;

        for (String fila : filas) {
            String[] valores = fila.split(";");
            for (String v : valores) {
                int val = Integer.parseInt(v.trim());
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
    }

    private int parsearTurno(String linea) {
        int inicio = linea.indexOf("TURNO=");
        int fin = linea.indexOf(";", inicio);
        if (inicio >= 0 && fin > inicio) {
            return Integer.parseInt(linea.substring(inicio + 6, fin));
        }
        return 0;
    }

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
