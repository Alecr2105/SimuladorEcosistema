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
import Utils.ValidacionUtil;
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
    
    //Flags de control y verificacion de cambios de extensión:
    private boolean ecosistemaGenerado = false;   // Marca si se generó un ecosistema
    private boolean cambiosPendientes = false;    // Marca si se cambiaron extensiones después de generar
    
    

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
        
        //Deshabilitar botones:
        vista.getBtnIniciar().setEnabled(false);
        vista.getBtnPausar().setEnabled(false);
        
        //Deshabilitar extensiones al inicio:
        vista.getCmbMutacion().setEnabled(false);
        vista.getChkMutacionesGneticas().setEnabled(false);
        vista.getRbtnTerceraEspecie().setEnabled(false);
        
        //Listener rbtnSeleccionarEscenario:
        configurarListenerEscenario();
        
        ecosistemaGenerado = false;   //Marca si se generó un ecosistema
        cambiosPendientes = false;    //Todos los cambios
        
        //Marcamos cambios en extensiones:
        vista.getChkMutacionesGneticas().addActionListener(e -> {
            boolean activo = vista.getChkMutacionesGneticas().isSelected();
            vista.getCmbMutacion().setEnabled(activo);
        });
        vista.getChkMutacionesGneticas().addActionListener(e -> cambiosPendientes = true);
        vista.getRbtnTerceraEspecie().addActionListener(e -> cambiosPendientes = true);
        vista.getRbtnEspecieMutante().addActionListener(e -> cambiosPendientes = true);
        vista.getRbtnAliadasPresas().addActionListener(e -> cambiosPendientes = true);
        vista.getRbtnAliadosDepredadores().addActionListener(e -> cambiosPendientes = true);
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
                    log("Simulación detenida: se alcanzó el máximo de turnos (" + maxTurnos + ").");
                    finalizarSimulacion();
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
                    finalizarSimulacion();
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

    
    
    
    
    private void configurarListenerEscenario() {
        vista.getCmbEscenario().addActionListener(e -> {
            Object seleccionado = vista.getCmbEscenario().getSelectedItem();

            if (seleccionado == null || seleccionado.toString().trim().isEmpty() ||
                seleccionado.toString().equals("Seleccione un escenario")) {

                // Deshabilitar extensiones
                vista.getCmbMutacion().setEnabled(false);
                vista.getChkMutacionesGneticas().setEnabled(false);
                vista.getRbtnTerceraEspecie().setEnabled(false);
                
                vista.getRbtnEspecieMutante().setEnabled(false);
                vista.getRbtnAliadasPresas().setEnabled(false);
                vista.getRbtnAliadosDepredadores().setEnabled(false);
            } else {
                // Habilitar extensiones:
                vista.getChkMutacionesGneticas().setEnabled(true);
                vista.getRbtnTerceraEspecie().setEnabled(true);
                
                vista.getRbtnEspecieMutante().setEnabled(true);
                vista.getRbtnAliadasPresas().setEnabled(true);
                vista.getRbtnAliadosDepredadores().setEnabled(true);
            }
        });
    }
    
    
    
    
    
    
    
    
    
    
    
    
   
    
    private boolean validarExtensiones() {
        // Tercera especie
        if (!ValidacionUtil.terceraEspecieValida(
                vista.isTerceraEspecieActiva(),
                vista.getRbtnEspecieMutante().isSelected(),
                vista.getRbtnAliadasPresas().isSelected(),
                vista.getRbtnAliadosDepredadores().isSelected())) {

            JOptionPane.showMessageDialog(vista,
                    "Debe seleccionar una variante de la tercera especie.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Mutaciones
        if (!ValidacionUtil.mutacionValida(
                vista.isMutacionesActivadas(),
                vista.getTipoMutacionSeleccionado())) {

            JOptionPane.showMessageDialog(vista,
                    "Debe seleccionar un tipo de mutación.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
    
    
    
    
    
    
    
    
    
    
   
    
    
    
    
    
    public void generarEscenarioDesdeVista() {
        //Validacion obligatoria de escenario base:
        Object seleccionado = vista.getCmbEscenario().getSelectedItem();
        String selec = seleccionado.toString().trim();
        if (selec.isEmpty() || selec.equals("Seleccione un escenario")) {
            JOptionPane.showMessageDialog(vista,
                "Debe seleccionar un escenario para generar la simulación.",
                "Datos incompletos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2️⃣ Validar extensiones (tercera especie y mutaciones)
        if (!validarExtensiones()) return;

        // 3️⃣ Obtener datos del escenario
        String opcion = (String) vista.getCmbEscenario().getSelectedItem();
        int presas, depredadores;

        switch (opcion) {
            case "Depredadores dominan" -> { presas = 10; depredadores = 30; }
            case "Presas dominan" -> { presas = 35; depredadores = 5; }
            default -> { presas = 20; depredadores = 20; } // Equilibrado
        }

        // 4️⃣ Obtener datos de la tercera especie
        int terceras = 0;
        String varianteTercera = null;
        if (vista.isTerceraEspecieActiva()) {
            varianteTercera = vista.getOpcionTerceraEspecie(); // Mutante, AliadaPresas, AliadaDepredadores
            terceras = 10;
        }

        // 5️⃣ Obtener datos de mutaciones
        boolean mutacionesActivas = vista.isMutacionesActivadas();
        String tipoMutacion = mutacionesActivas ? vista.getTipoMutacionSeleccionado() : null;

        // 6️⃣ Reiniciar variables de simulación
        turnos = 0;
        faseDepredadores = true;

        //Generar ecosistema:
        servicio.generarEscenario(presas, depredadores, terceras, varianteTercera, mutacionesActivas, tipoMutacion);

        //Actualizar tabla y guardar datos iniciales:
        actualizarTabla(vista.getTblEcosistema());
        servicio.guardarDatosIniciales(presas, depredadores, maxTurnos, opcion);
        servicio.guardarEstadoTurno(0, opcion);

        //Reseateamos los flags de control:
        ecosistemaGenerado = true;
        cambiosPendientes = false;
        
        //Habilitar botones:
        vista.getBtnIniciar().setEnabled(true);
        vista.getBtnPausar().setEnabled(true);
    }
    
    
    

    public void iniciarSimulacion() {
        if (!ecosistemaGenerado) {
            JOptionPane.showMessageDialog(vista,
                "Debe generar un ecosistema antes de iniciar la simulación.",
                "Acción no permitida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cambiosPendientes) {
            JOptionPane.showMessageDialog(vista,
                "Ha modificado extensiones después de generar el ecosistema.\n" +
                "Debe volver a presionar 'Generar ecosistema' antes de iniciar.",
                "Cambios pendientes",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Bloqueamos los controles para que no se puedan cambiar durante la simulación:
        vista.getCmbEscenario().setEnabled(false);
        vista.getChkMutacionesGneticas().setEnabled(false);
        vista.getRbtnTerceraEspecie().setEnabled(false);
        
        vista.getRbtnEspecieMutante().setEnabled(false);
        vista.getRbtnAliadasPresas().setEnabled(false);
        vista.getRbtnAliadosDepredadores().setEnabled(false);
        vista.getCmbMutacion().setEnabled(false);
        vista.getBtnGenerar().setEnabled(false);  // bloquear botón Generar
        
        
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
   
    
    
    
    
    
    private void finalizarSimulacion() {
        //Detenemos el timer si sigue activo:
        if (timer.isRunning()) {
            timer.stop();
        }
        
        //Deshabilitamos los botones de control:
        vista.getBtnIniciar().setEnabled(false);
        vista.getBtnPausar().setEnabled(false);

        //Cambiar texto del botón GENERAR ECOSISTEMA:
        vista.getBtnGenerar().setText("Nuevo ecosistema");
        vista.getBtnGenerar().setEnabled(true);
            
        //Evitamos presionar sin haber generado un nuevo ecosistema:
        vista.getBtnIniciar().setEnabled(false);
        vista.getBtnPausar().setEnabled(false);
        
        //Habilitamos controles:
        vista.getCmbEscenario().setEnabled(true);
        vista.getCmbMutacion().setEnabled(false);
        vista.getChkMutacionesGneticas().setEnabled(false); // se habilita solo al seleccionar escenario
        vista.getRbtnTerceraEspecie().setEnabled(false);   // igual
        vista.getRbtnEspecieMutante().setEnabled(false);
        vista.getRbtnAliadasPresas().setEnabled(false);
        vista.getRbtnAliadosDepredadores().setEnabled(false);
        
        //Reseteamos flags de control:
        ecosistemaGenerado = false;
        cambiosPendientes = false;
        
        vista.habilitarTabReporte();
        // Mostrar reporte
        vista.mostrarReporte();
        
        //Liampiamos campos:
        vista.limpiarCampos();
    }

}
