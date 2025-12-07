
package Controller;

import Model.ReporteDatos;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

public class ReporteController {
    private JPanel pnlGraficoPresasDepredadores;
    private JPanel pnlGraficoOcupacion;
    
    public ReporteController(JPanel pnl1, JPanel pnl2) {
        this.pnlGraficoPresasDepredadores = pnl1;
        this.pnlGraficoOcupacion = pnl2;
    }
    
    
    public void dibujarGraficoPresasDepredadores(ReporteDatos datos) {
        try {
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Presas", (Number) datos.getPresasFinales());
            dataset.setValue("Depredadores", (Number) datos.getDepredadoresFinales());

            JFreeChart chart = ChartFactory.createPieChart(
                    "Relación Presas vs Depredadores",
                    dataset,
                    true,
                    true,
                    false
            );

            //Estilo del título:
            TextTitle title = chart.getTitle();
            title.setFont(new Font("Nirmala UI", Font.BOLD, 18));  //Fuente, estilo y tamaño
            title.setPaint(new Color(0,0,128));                    //Color del título
            
            org.jfree.chart.plot.PiePlot plot = (org.jfree.chart.plot.PiePlot) chart.getPlot();
            
            //Colores de las secciones:
            plot.setSectionPaint("Presas", java.awt.Color.BLUE);       
            plot.setSectionPaint("Depredadores", java.awt.Color.LIGHT_GRAY);   
            plot.setBackgroundPaint(java.awt.Color.WHITE);//Fondo del gráfico
            
            //Borde del gráfico:
            plot.setOutlineVisible(true);
            plot.setOutlinePaint(Color.DARK_GRAY);
            
            //Separación de secciones (explode):
            plot.setExplodePercent("Presas", 0.10);

            //Etiquetas:
            plot.setSimpleLabels(true);
            plot.setLabelFont(new Font("Arial", Font.BOLD, 12));
            plot.setLabelPaint(Color.BLACK);
            plot.setLabelBackgroundPaint(Color.WHITE);  // fondo blanco para que resalte
            plot.setLabelOutlinePaint(Color.DARK_GRAY);
            plot.setLabelShadowPaint(Color.LIGHT_GRAY);
            plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
                "{0}: {1}" // muestra: Nombre: Valor
            ));
            
            ChartPanel panel = new ChartPanel(chart);
            
            pnlGraficoPresasDepredadores.removeAll();
            pnlGraficoPresasDepredadores.add(panel, BorderLayout.CENTER);
            pnlGraficoPresasDepredadores.validate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al dibujar gráfico Presas-Depredadores: " + ex.getMessage());
        }
    }

    
    
    
    public void dibujarGraficoOcupacion(ReporteDatos datos) {
        try {
            //Crear dataset:
            DefaultPieDataset dataset = new DefaultPieDataset();

            int ocupadas = datos.getCeldasOcupadas();
            int libres = datos.getTotalCeldas() - ocupadas;

            dataset.setValue("Ocupadas", (Number) ocupadas);
            dataset.setValue("Libres", (Number) libres);

            //Creamos gráfico:
            JFreeChart chart = ChartFactory.createPieChart(
                    "Ocupación del Ecosistema",
                    dataset,
                    true,
                    true,
                    false
            );

            //Estilo del título:
            TextTitle title = chart.getTitle();
            title.setFont(new Font("Nirmala UI", Font.BOLD, 18));  // Fuente, estilo y tamaño
            title.setPaint(new Color(0,0,128));     
            
            //Configuración del PiePlot:
            org.jfree.chart.plot.PiePlot plot = (org.jfree.chart.plot.PiePlot) chart.getPlot();
            
            //Colores en las secciones:
            plot.setSectionPaint("Ocupadas", java.awt.Color.BLUE);
            plot.setSectionPaint("Libres", java.awt.Color.LIGHT_GRAY);
            plot.setBackgroundPaint(java.awt.Color.WHITE);

            
            //Borde del gráfico
            plot.setOutlineVisible(true);
            plot.setOutlinePaint(Color.DARK_GRAY);

            // Separación de secciones (explode)
            plot.setExplodePercent("Ocupadas", 0.10);  // separar sección ocupadas si quieres destacar

            // Etiquetas más claras
            plot.setSimpleLabels(true);  // coloca etiquetas fuera de la sección
            plot.setLabelFont(new Font("Arial", Font.BOLD, 12));
            plot.setLabelPaint(Color.BLACK);
            plot.setLabelBackgroundPaint(Color.WHITE);
            plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
                    "{0}: {1}"  // muestra: Nombre: Valor
            ));
            
            //Configuracion de ChartPanel:
            ChartPanel panel = new ChartPanel(chart);
            
            pnlGraficoOcupacion.removeAll();
            pnlGraficoOcupacion.add(panel, BorderLayout.CENTER);
            pnlGraficoOcupacion.validate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al dibujar gráfico de ocupación: " + ex.getMessage());
        }
    }
}
