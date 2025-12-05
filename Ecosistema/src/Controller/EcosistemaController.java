package Controller;

import Business.EcosistemaService;
import View.frmPrincipal;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import Model.Celda;
import Model.Animal;
import Model.Presa;
import Model.Depredador;
import Model.TerceraEspecie;
import javax.swing.JOptionPane;

public class EcosistemaController {

    private final EcosistemaService servicio;
    private final frmPrincipal vista;
    private final ImageIcon iconVacio;
    private final ImageIcon iconPresa;
    private final ImageIcon iconDepredador;
    private final ImageIcon iconMutante;
    private final ImageIcon iconAliadaPresas;
    private final ImageIcon iconAliadaDepredadores;
    private final ImageIcon iconDepredadorFurioso;
    private final ImageIcon iconPresaVenenosa;

    private Timer timer;
    private int turnos;
    private final int DELAY = 600;
    private boolean faseDepredadores = true;

    private String escenarioActual = "Equilibrado";
    private int maxTurnos = 15;

    private boolean mutacionesActivas = false;
    private String tipoMutacionActual = null;

    public EcosistemaController(frmPrincipal vista) {
        this.vista = vista;
        this.servicio = new EcosistemaService();

        iconVacio = new ImageIcon("src/img/vacio.png");
        iconPresa = new ImageIcon("src/img/presa.png");
        iconDepredador = new ImageIcon("src/img/depredador.png");
        iconMutante = new ImageIcon("src/img/mutante.png");
        iconAliadaPresas = new ImageIcon("src/img/aliada_presas.png");
        iconAliadaDepredadores = new ImageIcon("src/img/aliada_depredadores.png");
        iconDepredadorFurioso = new ImageIcon(getClass().getResource("/img/depredador_furioso.png"));
        iconPresaVenenosa = new ImageIcon(getClass().getResource("/img/presa_venenosa.png"));

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
                actualizarTabla(vista.getTblEcosistema());
                log("Movimiento de depredadores en turno " + (turnos + 1));
                faseDepredadores = false;
            } else {
                servicio.moverPresas();
                servicio.moverTerceraEspecie();
                servicio.aplicarFinDeTurnoBasico();

                turnos++; // turno completo (depredadores + presas + tercera)

                actualizarTabla(vista.getTblEcosistema());
                for (String ev : servicio.consumirEventosTurno()) {
                    log(ev);
                }

                int presas = servicio.getTotalPresas();
                int depredadores = servicio.getTotalDepredadores();
                int terceras = servicio.getTotalTerceraEspecie();

                log("Turno " + turnos + " completado - Presas: " + presas
                        + ", Depredadores: " + depredadores
                        + ", Tercera especie: " + terceras);

                // Guardar estado del turno
                servicio.guardarEstadoTurno(turnos, escenarioActual);

                // Verificar máximo de turnos
                if (turnos >= maxTurnos) {
                    timer.stop();
                    log("Simulación detenida: se alcanzó el máximo de turnos (" + maxTurnos + ").");
                }

                // Opcional: log de extinciones
                if (presas == 0) {
                    log(">> Las presas se extinguieron en el turno " + turnos);
                }
                if (depredadores == 0) {
                    log(">> Los depredadores se extinguieron en el turno " + turnos);
                }
                if (presas == 0 && depredadores == 0 && terceras == 0) {
                    log(">> Todas las especies se extinguieron. Fin de la simulación.");
                    timer.stop();
                }

                faseDepredadores = true;
            }
        });
    }

    public void actualizarTabla(JTable tabla) {
        Celda[][] celdas = servicio.getEcosistema().getMatriz();

        for (int i = 0; i < celdas.length; i++) {
            for (int j = 0; j < celdas[i].length; j++) {

                if (celdas[i][j].estaVacia()) {
                    tabla.setValueAt(iconVacio, i, j);
                    continue;
                }

                Animal a = celdas[i][j].getAnimal();

                // 🟢 PRESA (normal o venenosa)
                if (a instanceof Presa p) {
                    if (p.isVenenosa()
                            && iconPresaVenenosa != null
                            && iconPresaVenenosa.getIconWidth() > 0) {
                        tabla.setValueAt(iconPresaVenenosa, i, j);
                    } else {
                        tabla.setValueAt(iconPresa, i, j);
                    }

                    // 🔴 DEPREDADOR (normal o furioso)
                } else if (a instanceof Depredador d) {
                    if (d.isFurioso()
                            && iconDepredadorFurioso != null
                            && iconDepredadorFurioso.getIconWidth() > 0) {
                        tabla.setValueAt(iconDepredadorFurioso, i, j);
                    } else {
                        tabla.setValueAt(iconDepredador, i, j);
                    }

                    // 🟣 TERCERA ESPECIE (mutante / aliada presas / aliada depredadores)
                } else if (a instanceof TerceraEspecie te) {
                    switch (te.getVariante()) {
                        case "Mutante" ->
                            tabla.setValueAt(iconMutante, i, j);
                        case "AliadaPresas" ->
                            tabla.setValueAt(iconAliadaPresas, i, j);
                        case "AliadaDepredadores" ->
                            tabla.setValueAt(iconAliadaDepredadores, i, j);
                        default ->
                            tabla.setValueAt(iconMutante, i, j); // fallback
                    }

                } else {
                    // por si en el futuro aparece otro tipo
                    tabla.setValueAt(iconVacio, i, j);
                }
            }
        }
    }

    // 🔹 NUEVO: generar escenario según JComboBox
    public void generarEscenarioDesdeVista() {
        vista.getTxtMovimientos().setText("");

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
        mutacionesActivas = vista.isMutacionesActivadas();
        tipoMutacionActual = null;

        if (mutacionesActivas) {
            tipoMutacionActual = vista.getTipoMutacionSeleccionado();
            if (tipoMutacionActual == null) {
                JOptionPane.showMessageDialog(vista,
                        "Debes seleccionar un tipo de mutación (Furia o Veneno).",
                        "Mutaciones genéticas",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // ahora usamos el método nuevo del service
        servicio.generarEscenario(presas, depredadores, terceras, varianteTercera,
                mutacionesActivas, tipoMutacionActual);

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

    private void log(String mensaje) {
        vista.getTxtMovimientos().append(mensaje + "\n");
    }

}
