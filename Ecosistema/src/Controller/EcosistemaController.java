package Controller;

import Business.EcosistemaService;
import View.frmPrincipal;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public class EcosistemaController {

    private final EcosistemaService servicio;
    private final frmPrincipal vista;
    private final ImageIcon iconVacio;
    private final ImageIcon iconPresa;
    private final ImageIcon iconDepredador;
    private final ImageIcon iconTercera;

    private Timer timer;
    private int turnos;
    private final int DELAY = 600;
    private boolean faseDepredadores = true;

    private String escenarioActual = "Equilibrado";
    private int maxTurnos = 50;

    public EcosistemaController(frmPrincipal vista) {
        this.vista = vista;
        this.servicio = new EcosistemaService();

        iconVacio = new ImageIcon("src/img/vacio.png");
        iconPresa = new ImageIcon("src/img/presa.png");
        iconDepredador = new ImageIcon("src/img/depredador.png");
        iconTercera = new ImageIcon("src/img/tercera.png"); //dddd crea una imagen para esta especie

        configurarTabla(vista.getTblEcosistema());
        configurarTimer();
    }

    private void configurarTabla(JTable tabla) {
        DefaultTableModel model = new DefaultTableModel(10, 10) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return ImageIcon.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla.setModel(model);
        tabla.setRowHeight(45);
    }

    private void configurarTimer() {
        timer = new Timer(DELAY, e -> {
            if (faseDepredadores) {
                servicio.moverDepredadores();
                faseDepredadores = false;
            } else {
                servicio.moverPresas();
                servicio.moverTerceraEspecie();      // 👈 NUEVO
                servicio.aplicarFinDeTurnoBasico();
                faseDepredadores = true;
                turnos++;
                servicio.guardarEstadoTurno(turnos, escenarioActual);
            }

            actualizarTabla(vista.getTblEcosistema());
        });
    }

    public void actualizarTabla(JTable tabla) {
        int[][] matriz = servicio.getMatrizNumerica();
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                switch (matriz[i][j]) {
                    case 0 ->
                        tabla.setValueAt(iconVacio, i, j);
                    case 1 ->
                        tabla.setValueAt(iconPresa, i, j);
                    case 2 ->
                        tabla.setValueAt(iconDepredador, i, j);
                    case 3 ->
                        tabla.setValueAt(iconTercera, i, j);
                }

            }
        }
    }

    // 🔹 NUEVO: generar escenario según JComboBox
    public void generarEscenarioDesdeVista() {
        String opcion = (String) vista.getCmbEscenario().getSelectedItem();

        int presas;
        int depredadores;

        switch (opcion) {
            case "Depredadores dominan" -> {
                escenarioActual = "Depredadores dominan";
                presas = 10;
                depredadores = 30;
            }
            case "Presas dominan" -> {
                escenarioActual = "Presas dominan";
                presas = 35;
                depredadores = 5;
            }
            default -> {
                escenarioActual = "Equilibrado";
                presas = 20;
                depredadores = 20;
            }
        }

        // NUEVO: tercera especie
        int terceras = 0;
        String varianteTercera = null;

        if (vista.isTerceraEspecieActiva()) {
            varianteTercera = vista.getOpcionTerceraEspecie();
            // Puedes ajustar cuántos individuos iniciales de tercera especie
            terceras = 10;
        }

        turnos = 0;
        faseDepredadores = true;

        // ahora usamos el método nuevo del service
        servicio.generarEscenario(presas, depredadores, terceras, varianteTercera);
        actualizarTabla(vista.getTblEcosistema());

        servicio.guardarDatosIniciales(presas, depredadores, maxTurnos, escenarioActual);
        servicio.guardarEstadoTurno(0, escenarioActual);
    }

    public void iniciarSimulacion() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void pausarSimulacion() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    public void siguienteTurnoManual() {
        servicio.moverDepredadores();
        actualizarTabla(vista.getTblEcosistema());

        servicio.moverPresas();
        servicio.moverTerceraEspecie();   // 👈 NUEVO
        servicio.aplicarFinDeTurnoBasico();
        actualizarTabla(vista.getTblEcosistema());

        turnos++;
        servicio.guardarEstadoTurno(turnos, escenarioActual);
    }

    public int getTurnos() {
        return turnos;
    }
}
