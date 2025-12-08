package Business;

import Controller.AuthController;
import Model.ReporteDatos;

//Librería itext:
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.text.DecimalFormat;

//Librería email:
import Utils.EmailService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jfree.chart.JFreeChart;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReporteService {

    private static final Logger LOGGER = Logger.getLogger(ReporteService.class.getName());
    private static final String ARCHIVO_ESTADOS = "estado_turnos.txt";
    private static final int TAM_MATRIZ = 10;

    private final EmailService emailService = new EmailService();

    //Método que siempre devuelve un ReporteDatos, aunque haya
    //problemas leyendo el archivo.
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
            List<String> lineasTurno = new ArrayList<>();

            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("---TURNO=")) {
                    //Cerramos turno anterior si estaba en proceso:
                    if (turnoEnProceso != null && !lineasTurno.isEmpty()) {
                        seLeyeronTurnos |= procesarTurno(turnoEnProceso, lineasTurno, reporteActual);
                    }

                    turnoEnProceso = parsearTurnoSeguro(linea);
                    lineasTurno = new ArrayList<>();

                    //Si es un nuevo ciclo (turno 0) reiniciamos acomuladores:
                    if (turnoEnProceso != null && turnoEnProceso == 0) {
                        reporteActual = new ReporteDatos(TAM_MATRIZ * TAM_MATRIZ);
                    }
                } else if (linea.trim().isEmpty()) {
                    if (turnoEnProceso != null && !lineasTurno.isEmpty()) {
                        seLeyeronTurnos |= procesarTurno(turnoEnProceso, lineasTurno, reporteActual);
                        turnoEnProceso = null;
                        lineasTurno = new ArrayList<>();
                    }
                } else {
                    lineasTurno.add(linea.trim());
                }
            }

            //Procesamos el último turno pendiente:
            if (turnoEnProceso != null && !lineasTurno.isEmpty()) {
                seLeyeronTurnos |= procesarTurno(turnoEnProceso, lineasTurno, reporteActual);
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

    //Creamos un reporte vacío (todo en 0) esto para evitar romper la UI.
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

    private boolean procesarTurno(int turno, List<String> lineas, ReporteDatos reporte) {
        if (lineas == null || lineas.isEmpty()) {
            return false;
        }

        // Intentar formato antiguo basado en matrices
        if (procesarTurnoMatriz(turno, lineas, reporte)) {
            return true;
        }

        // Intentar formato de log de movimientos
        return procesarTurnoEventos(turno, lineas, reporte);
    }

    //Procesa un bloque de 10 filas (un turno) y actualizamos el reporte.
    private boolean procesarTurnoMatriz(int turno, List<String> filas, ReporteDatos reporte) {
        if (filas.size() != TAM_MATRIZ) {
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

            boolean formatoConColumnas = true;
            for (String fila : filas) {
                if (!fila.contains(";")) {
                    formatoConColumnas = false;
                    break;
                }
            }

            if (!formatoConColumnas) {
                return false;
            }

            for (String v : valores) {
                int val;
                try {
                    val = Integer.parseInt(v.trim());
                } catch (NumberFormatException ex) {

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

    private boolean procesarTurnoEventos(int turno, List<String> eventos, ReporteDatos reporte) {
        Pattern resumenPattern = Pattern.compile(
                "Turno\\s+(\\d+)\\s+completado\\s+-\\s+Presas:\\s*(\\d+),\\s*Depredadores:\\s*(\\d+),\\s*Tercera especie:\\s*(\\d+)");

        Integer presas = null;
        Integer depredadores = null;
        Integer terceras = null;

        for (String linea : eventos) {
            Matcher matcher = resumenPattern.matcher(linea);
            if (matcher.find()) {
                try {
                    int turnoLinea = Integer.parseInt(matcher.group(1));
                    presas = Integer.parseInt(matcher.group(2));
                    depredadores = Integer.parseInt(matcher.group(3));
                    terceras = Integer.parseInt(matcher.group(4));

                    if (turnoLinea != turno) {
                        LOGGER.log(Level.FINE,
                                "El resumen indica el turno {0} pero el encabezado es {1}. Se usará el encabezado.",
                                new Object[]{turnoLinea, turno});
                    }
                } catch (NumberFormatException ex) {
                    LOGGER.log(Level.WARNING, "No se pudieron parsear los conteos del turno {0}.", turno);
                }
                break;
            }
        }

        if (presas == null || depredadores == null || terceras == null) {
            if (turno == 0) {
                LOGGER.log(Level.FINE,
                        "El turno 0 no contiene un resumen de conteos. Se omitirá sin marcarlo como error.");
            } else {
                LOGGER.log(Level.WARNING,
                        "El turno {0} no contiene un resumen de conteos. Se ignorará este bloque.",
                        turno);
            }
            return false;
        }

        int ocupadas = presas + depredadores + terceras;

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

    //Genera un PDF a partir de los datos ya cargados.
    public String generarPdfConGraficos(ReporteDatos datos,
            JFreeChart graficoPresas,
            JFreeChart graficoOcupacion,
            String nombreArchivo)
            throws DocumentException, IOException {

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
        document.open();

        // ======= PÁGINA 1 – RESUMEN NUMÉRICO =======
        document.add(new Paragraph("Reporte de simulación",
                new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 24, com.lowagie.text.Font.BOLD)));

        document.add(new Paragraph("\n"));

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        document.add(new Paragraph("Turnos ejecutados: " + datos.getTotalTurnos()));
        document.add(new Paragraph("Presas finales: " + datos.getPresasFinales()));
        document.add(new Paragraph("Depredadores finales: " + datos.getDepredadoresFinales()));
        document.add(new Paragraph("Tercera especie final: " + datos.getTerceraEspecieFinal()));
        document.add(new Paragraph("Ocupación: " + datos.getCeldasOcupadas() + "/" + datos.getTotalCeldas()));
        document.add(new Paragraph("Porcentaje de ocupación: "
                + decimalFormat.format(datos.getPorcentajeOcupacion()) + " %"));

        String mensajeExtincionPresas = datos.getTurnoExtincionPresas() == null
                ? "No se extinguieron las presas"
                : "Se extinguieron las presas en el turno " + datos.getTurnoExtincionPresas();
        String mensajeExtincionDepredadores = datos.getTurnoExtincionDepredadores() == null
                ? "No se extinguieron los depredadores"
                : "Se extinguieron los depredadores en el turno " + datos.getTurnoExtincionDepredadores();

        document.add(new Paragraph(mensajeExtincionPresas));
        document.add(new Paragraph(mensajeExtincionDepredadores));

        document.newPage();

        // ======= PÁGINA 2 – GRÁFICOS =======
        document.add(new Paragraph("Gráficos de la simulación",
                new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 24, com.lowagie.text.Font.BOLD)));

        document.add(new Paragraph("\n")); // espacio

        // Gráfico de presas vs depredadores
        java.awt.Image img1 = graficoPresas.createBufferedImage(900, 450);
        com.lowagie.text.Image pdfImg1 = com.lowagie.text.Image.getInstance(writer, img1, 1f);
        pdfImg1.scaleToFit(780, 320);
        pdfImg1.setAlignment(com.lowagie.text.Image.ALIGN_CENTER);
        document.add(pdfImg1);

        document.add(new Paragraph("\n"));

        // Gráfico de ocupación
        java.awt.Image img2 = graficoOcupacion.createBufferedImage(900, 450);
        com.lowagie.text.Image pdfImg2 = com.lowagie.text.Image.getInstance(writer, img2, 1f);
        pdfImg2.scaleToFit(780, 320);
        pdfImg2.setAlignment(com.lowagie.text.Image.ALIGN_CENTER);
        document.add(pdfImg2);

        document.add(new Paragraph("\nOcupadas: " + datos.getCeldasOcupadas()
                + " / " + datos.getTotalCeldas()
                + " (" + decimalFormat.format(datos.getPorcentajeOcupacion()) + " %)"));

        document.close();
        return nombreArchivo;
    }

    public void enviarPdfPorCorreo(String rutaPdf) {
        if (AuthController.usuarioActual == null) {
            return;
        }

        String destinatario = AuthController.usuarioActual.getCorreo();

        if (destinatario == null || destinatario.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "El usuario no tiene correo registrado.");
            return;
        }

        try {
            emailService.enviarCorreoConAdjunto(
                    destinatario,
                    "Reporte de Ecosistema",
                    "Adjunto el reporte de la simulación.",
                    rutaPdf
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al enviar correo: " + ex.getMessage());
        }
    }
}
