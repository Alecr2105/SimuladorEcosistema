package View;

import Business.ReporteService;
import Controller.AuthController;
import Controller.EcosistemaController;
import Model.ReporteDatos;
import Utils.EmailService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class frmPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(frmPrincipal.class.getName());
    private EcosistemaController ecosistemaController;
    private final ReporteService reporteService = new ReporteService();
    private final EmailService emailService = new EmailService();
    private boolean reporteDesbloqueado = false;

    public frmPrincipal() {
        initComponents();
        setLocationRelativeTo(null);//centramos pantalla:
        cmbEscenario.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[]{"Seleccione un escenario","Equilibrado", "Depredadores dominan", "Presas dominan"}
        ));
        cmbEscenario.setSelectedIndex(0);
        
        cmbMutacion.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[]{"Seleccione una mutación","Furia (depredadores)", "Veneno (presas)"}
        ));
        cmbMutacion.setSelectedIndex(0);

        //Personalizacion de tabs con FlatLaf
        tbpPrincipal.putClientProperty("JTabbedPane.tabHeight", 40);
        tbpPrincipal.putClientProperty("JTabbedPane.showTabSeparators", true);
        tbpPrincipal.putClientProperty("JTabbedPane.tabSeparatorsFullHeight", true);
        tbpPrincipal.putClientProperty("JTabbedPane.tabAreaAlignment", "center");
        tbpPrincipal.putClientProperty("JTabbedPane.tabInsets", new java.awt.Insets(6, 20, 6, 20));
        pnlTerceraEspecie.setVisible(false);

        pnlGraficoPresasDepredadores.setLayout(new BorderLayout());
        pnlGraficoOcupacion.setLayout(new BorderLayout());

        //Borde redondeado:
        int radio = 12;
        txtCedula.setBorder(new RoundedBorder(radio));
        pwdLogin.setBorder(new RoundedBorder(radio));
        txtRegCedula.setBorder(new RoundedBorder(radio));
        txtRegNombre.setBorder(new RoundedBorder(radio));
        txtRegCorreo.setBorder(new RoundedBorder(radio));
        pwpReg.setBorder(new RoundedBorder(radio));

        // ---- Placeholders ----
        setPlaceholder(txtCedula, "Cédula");
        setPlaceholder(txtRegCedula, "Cédula");
        setPlaceholder(txtRegNombre, "Nombre completo");
        setPlaceholder(txtRegCorreo, "Correo electrónico");
        setPlaceholderPassword(pwdLogin, "Contraseña");
        setPlaceholderPassword(pwpReg, "Contraseña");

        bloquearAccesoEcosistema();
        bloquearAccesoReporte();

        //Agregar los listeners con los métods mostrarLogin y mostrarRegistro.
        //Cursores tipo mano...
        jlNoCuenta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jlTieneCuenta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        //Cambiar de login a registro
        jlNoCuenta.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mostrarRegistro();
            }
        });
        //Cambiar de registro a login
        jlTieneCuenta.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mostrarLogin();
            }
        });
        ecosistemaController = new EcosistemaController(this);
    }

    public void mostrarCamposRegistro() {
        txtRegCedula.setVisible(true);
        txtRegNombre.setVisible(true);
        dtRegFecha.setVisible(true);
        txtRegCorreo.setVisible(true);
        pwpReg.setVisible(true);

        rdbRegMasculino.setVisible(true);
        rdbRegFemenino.setVisible(true);

        btnCrearCuenta.setVisible(true);

        jlRegistroUsuario.setVisible(true);
        jlTieneCuenta.setVisible(true);
    }

    public void limpiarCampos() {
        // LOGIN
        txtCedula.setText("");
        pwdLogin.setText("");

        // REGISTRO
        txtRegCedula.setText("");
        txtRegNombre.setText("");
        txtRegCorreo.setText("");
        pwpReg.setText("");
        dtRegFecha.setDate(null);

        rdbRegMasculino.setSelected(false);
        rdbRegFemenino.setSelected(false);

    }

    // Placeholder para JTextField
    public void setPlaceholder(JTextField txt, String placeholder) {
        txt.setForeground(Color.GRAY);
        txt.setText(placeholder);

        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt.getText().equals(placeholder)) {
                    txt.setText("");
                    txt.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().isEmpty()) {
                    txt.setForeground(Color.GRAY);
                    txt.setText(placeholder);
                }
            }
        });
    }

    // Placeholder para JPasswordField
    public void setPlaceholderPassword(JPasswordField pwd, String placeholder) {
        pwd.setForeground(Color.GRAY);
        pwd.setText(placeholder);
        // mostrar texto (desactivar echo char)
        pwd.setEchoChar((char) 0);

        pwd.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String current = new String(pwd.getPassword());
                if (current.equals(placeholder)) {
                    pwd.setText("");
                    pwd.setForeground(Color.BLACK);
                    // restablece echo char para ocultar texto (valor por defecto)
                    pwd.setEchoChar('\u2022');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String current = new String(pwd.getPassword());
                if (current.isEmpty()) {
                    pwd.setForeground(Color.GRAY);
                    pwd.setText(placeholder);
                    pwd.setEchoChar((char) 0);
                }
            }
        });
    }

    
    
    
    
    
    
    
    //Bloqueos:
    //***************************************************************
    //No permite volver al tab inicio a menos que se cierre la sesion:
    public void bloquearAccesoInicio() {
        tbpPrincipal.setEnabledAt(0, false);
    }

    //Desbloquear el tab de inicio al cerrar sesión:
    public void desbloquearAccesoInicio() {
        tbpPrincipal.setEnabledAt(0, true);
    }

    public void setReporteDesbloqueado(boolean valor) {
        this.reporteDesbloqueado = valor;
    }

    public void bloquearAccesoReporte() {
        tbpPrincipal.setEnabledAt(2, false); // Tab Reporte deshabilitado

        tbpPrincipal.addChangeListener(e -> {
            if (tbpPrincipal.getSelectedIndex() == 2 && !reporteDesbloqueado) {
                JOptionPane.showMessageDialog(this,
                        "El reporte solo está disponible cuando la simulación haya finalizado.");
                tbpPrincipal.setSelectedIndex(1); // Regresa a Ecosistemas
            }
        });
    }

    public void habilitarTabReporte() {
        tbpPrincipal.setEnabledAt(2, true);  
        this.reporteDesbloqueado = true;    
    }

    //No permite ingresar al tab de ecosistema, hasta que no se haya hecho el LOGIN:
    public void bloquearAccesoEcosistema() {
        getTbpPrincipal().addChangeListener(e -> {
            if (getTbpPrincipal().getSelectedComponent() == getPnlEcosistemas()) {
                if (!AuthController.sesionIniciada) {
                    JOptionPane.showMessageDialog(this,
                            "Debe iniciar sesión para acceder al ecosistema.");
                    getTbpPrincipal().setSelectedComponent(getPnlInicio());
                }
            }
        });
    }
    
    
    
    

    //Getters de los componentes:
    //***************************************************************
    // Panels principales
    public javax.swing.JTabbedPane getTbpPrincipal() {
        return tbpPrincipal;
    }

    public javax.swing.JPanel getPnlEcosistemas() {
        return pnlEcosistemas;
    }

    public javax.swing.JPanel getPnlInicio() {
        return pnlInicio;
    }

    public javax.swing.JTable getTblEcosistema() {
        return tblEcosistema;
    }
    
    
    
    
    
    // LOGIN(pnlLogin):
    public javax.swing.JTextField getTxtCedula() {
        return txtCedula;
    }

    public javax.swing.JPasswordField getPwdContrasena() {
        return pwdLogin;
    }

    public javax.swing.JButton getBtnCerrarSesion() {
        return btnCerrarSesion;
    }

    public javax.swing.JButton getBtnIngresar() {
        return btnIngresar;
    }

    public javax.swing.JLabel getLblNoCuenta() {
        return jlNoCuenta;
    }

    
    
    
    
    // REGISTRO(pnlRegistro):
    public javax.swing.JTextField getTxtRegCedula() {
        return txtRegCedula;
    }

    public javax.swing.JTextField getTxtRegNombre() {
        return txtRegNombre;
    }

    public javax.swing.JTextField getTxtRegCorreo() {
        return txtRegCorreo;
    }

    public javax.swing.JPasswordField getPwdReg() {
        return pwpReg;
    }

    public javax.swing.JButton getBtnRegistrar() {
        return btnCrearCuenta;
    }

    // RadioButtons de género
    public javax.swing.JRadioButton getRdbMaculino() {
        return rdbRegMasculino;
    }

    public javax.swing.JRadioButton getRdbFemenino() {
        return rdbRegFemenino;
    }

    public javax.swing.ButtonGroup getBtnGenero() {
        return btnGGenero;
    }

    
    
    
    
    
    
    
    //ECOSISTEMAS(pnlEcosistemas):
    public javax.swing.JButton getBtnIniciar() {
        return btnIniciar;
    }
    public javax.swing.JButton getBtnPausar() {
        return btnPausar;
    }
    public javax.swing.JButton getBtnGenerar() {
        return btnGenerarEcosistema;
    }
    public javax.swing.JRadioButton getRbtnTerceraEspecie() {
        return rbtnTerceraEspecie;
    }
    public javax.swing.JComboBox<String> getCmbEscenario() {
        return cmbEscenario;
    }
    public javax.swing.JComboBox<String> getCmbMutacion() {
        return cmbMutacion;
    }
    
    
    
    
    // Labels
    public javax.swing.JLabel getLblRegistro() {
        return jlRegistroUsuario;
    }

    public javax.swing.JLabel getLblTieneCuenta() {
        return jlTieneCuenta;
    }

    public javax.swing.JLabel getLblCambiarARegistro() {
        return jlNoCuenta;
    }

    public javax.swing.JLabel getLblCambiarALogin() {
        return jlTieneCuenta;
    }

    public com.toedter.calendar.JDateChooser getDtRegFecha() {
        return dtRegFecha;
    }

    
    
    
    
    
    
    
    //Para cerrar Sesión:
    public EcosistemaController getEcosistemaController() {
        return ecosistemaController;
    }

    
    
    
    
    
    
    //Métodos para mostrar LOGIN y REGISTRO:
    public void mostrarLogin() {
        ((java.awt.CardLayout) pnlContenedor.getLayout()).show(pnlContenedor, "card2");
    }

    public void mostrarRegistro() {
        ((java.awt.CardLayout) pnlContenedor.getLayout()).show(pnlContenedor, "card3");
    }

    public void mostrarReporte() {
        tbpPrincipal.setSelectedComponent(pnlReporte);

        // 1) Cargar datos del archivo (nunca será null gracias a ReporteService)
        ReporteDatos datos = null;
        try {
            datos = reporteService.cargarDatosDesdeArchivo();
        } catch (Exception e) {
            // Con la nueva versión de ReporteService esto casi nunca pasará,
            // pero por seguridad mostramos un mensaje y usamos un reporte vacío.
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los datos del archivo de estados.\n"
                    + "Se mostrará un reporte vacío.\nDetalle: " + e.getMessage(),
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            datos = new ReporteDatos(10 * 10); // 10x10
        }

        // 2) Siempre llenar la UI con lo que tengamos en 'datos'
        spnCantidadTurnos.setModel(new javax.swing.SpinnerNumberModel(datos.getTotalTurnos(), 0, 1000, 1));
        spnCantidadTurnos.setEnabled(false);

        txtExtincion1.setText(datos.getTurnoExtincionPresas() == null
                ? "N/A"
                : datos.getTurnoExtincionPresas().toString());

        txtExtincion2.setText(datos.getTurnoExtincionDepredadores() == null
                ? "N/A"
                : datos.getTurnoExtincionDepredadores().toString());

        // Dibujar gráficos
        dibujarGraficoPresasDepredadores(datos);
        dibujarGraficoOcupacion(datos);

        // 3) Intentar generar PDF y enviarlo por correo
        try {
            // Obtener los charts directamente desde los paneles
            JFreeChart graficoPresas
                    = ((ChartPanel) pnlGraficoPresasDepredadores.getComponent(0)).getChart();

            JFreeChart graficoOcupacion
                    = ((ChartPanel) pnlGraficoOcupacion.getComponent(0)).getChart();

            // Generar PDF con ambos gráficos (Opción A: una página por gráfico)
            String rutaPdf = reporteService.generarPdfConGraficos(
                    datos,
                    graficoPresas,
                    graficoOcupacion,
                    "reporte_simulacion.pdf"
            );

            enviarPdfPorCorreo(rutaPdf);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar o enviar el reporte PDF.\nDetalle: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGGenero = new javax.swing.ButtonGroup();
        jCheckBox1 = new javax.swing.JCheckBox();
        btngTerceraEspecie = new javax.swing.ButtonGroup();
        tbpPrincipal = new javax.swing.JTabbedPane();
        pnlInicio = new javax.swing.JPanel();
        pnlContenedor = new javax.swing.JPanel();
        pnlLogin = new javax.swing.JPanel();
        jlInicioSesion = new javax.swing.JLabel();
        txtCedula = new javax.swing.JTextField();
        pwdLogin = new javax.swing.JPasswordField();
        btnIngresar = new javax.swing.JButton();
        jlNoCuenta = new javax.swing.JLabel();
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR) {
        });
        jlIcono = new javax.swing.JLabel();
        pnlRegistro = new javax.swing.JPanel();
        jlRegistroUsuario = new javax.swing.JLabel();
        txtRegCedula = new javax.swing.JTextField();
        txtRegNombre = new javax.swing.JTextField();
        dtRegFecha = new com.toedter.calendar.JDateChooser();
        txtRegCorreo = new javax.swing.JTextField();
        rdbRegMasculino = new javax.swing.JRadioButton();
        rdbRegFemenino = new javax.swing.JRadioButton();
        pwpReg = new javax.swing.JPasswordField();
        btnCrearCuenta = new javax.swing.JButton();
        jlGenero = new javax.swing.JLabel();
        jlTieneCuenta = new javax.swing.JLabel();
        jlTieneCuenta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR) {
        });
        pnlEcosistemas = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEcosistema = new javax.swing.JTable();
        btnGenerarEcosistema = new javax.swing.JButton();
        btnIniciar = new javax.swing.JButton();
        btnPausar = new javax.swing.JButton();
        cmbEscenario = new javax.swing.JComboBox<>();
        rbtnTerceraEspecie = new javax.swing.JRadioButton();
        pnlTerceraEspecie = new javax.swing.JPanel();
        rbtnEspecieMutante = new javax.swing.JRadioButton();
        rbtnEspecieAliadaPresas = new javax.swing.JRadioButton();
        rbtnEspecieAliadaDepredadores = new javax.swing.JRadioButton();
        btnCerrarSesion = new javax.swing.JButton();
        jlSeleccionarEscenario = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtMovimientos = new javax.swing.JTextArea();
        cmbMutacion = new javax.swing.JComboBox<>();
        chkMutacionesGeneticas = new javax.swing.JCheckBox();
        jlSimuladorEcosistemas = new javax.swing.JLabel();
        jlMatriz10x10 = new javax.swing.JLabel();
        jlControles = new javax.swing.JLabel();
        jlConsolaEventos = new javax.swing.JLabel();
        jlExtensiones = new javax.swing.JLabel();
        pnlReporte = new javax.swing.JPanel();
        lblTurnos = new javax.swing.JLabel();
        spnCantidadTurnos = new javax.swing.JSpinner();
        pnlGraficoPresasDepredadores = new javax.swing.JPanel();
        lblGraficoPresasDepredadores = new javax.swing.JLabel();
        lblPrimeraExtincion = new javax.swing.JLabel();
        txtExtincion1 = new javax.swing.JTextField();
        lblSegundaExtincion = new javax.swing.JLabel();
        txtExtincion2 = new javax.swing.JTextField();
        pnlGraficoOcupacion = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        jCheckBox1.setText("jCheckBox1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximizedBounds(new java.awt.Rectangle(849, 490, 490, 490));
        setMinimumSize(new java.awt.Dimension(849, 525));

        tbpPrincipal.setBackground(new java.awt.Color(255, 255, 255));
        tbpPrincipal.setMaximumSize(new java.awt.Dimension(849, 490));
        tbpPrincipal.setMinimumSize(new java.awt.Dimension(849, 525));
        tbpPrincipal.setOpaque(true);
        tbpPrincipal.setPreferredSize(new java.awt.Dimension(1063, 639));

        pnlInicio.setBackground(new java.awt.Color(255, 255, 255));
        pnlInicio.setForeground(new java.awt.Color(255, 255, 255));
        pnlInicio.setMaximumSize(new java.awt.Dimension(849, 490));
        pnlInicio.setMinimumSize(new java.awt.Dimension(849, 525));
        pnlInicio.setPreferredSize(new java.awt.Dimension(1063, 639));
        pnlInicio.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlContenedor.setBackground(new java.awt.Color(255, 255, 255));
        pnlContenedor.setMaximumSize(new java.awt.Dimension(849, 490));
        pnlContenedor.setMinimumSize(new java.awt.Dimension(849, 525));
        pnlContenedor.setPreferredSize(new java.awt.Dimension(1063, 639));
        pnlContenedor.setLayout(new java.awt.CardLayout());

        pnlLogin.setBackground(new java.awt.Color(255, 255, 255));
        pnlLogin.setMaximumSize(new java.awt.Dimension(849, 490));
        pnlLogin.setMinimumSize(new java.awt.Dimension(849, 525));
        pnlLogin.setPreferredSize(new java.awt.Dimension(849, 490));
        pnlLogin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlInicioSesion.setBackground(new java.awt.Color(255, 255, 255));
        jlInicioSesion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jlInicioSesion.setForeground(new java.awt.Color(0, 51, 102));
        jlInicioSesion.setText("Inicio Sesión");
        pnlLogin.add(jlInicioSesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 140, -1, -1));

        txtCedula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCedulaActionPerformed(evt);
            }
        });
        pnlLogin.add(txtCedula, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 190, 250, 40));

        pwdLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pwdLoginActionPerformed(evt);
            }
        });
        pnlLogin.add(pwdLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 240, 250, 40));

        btnIngresar.setBackground(new java.awt.Color(0, 51, 102));
        btnIngresar.setForeground(new java.awt.Color(255, 255, 255));
        btnIngresar.setText("Ingresar");
        btnIngresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIngresarActionPerformed(evt);
            }
        });
        pnlLogin.add(btnIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 290, 250, 40));

        jlNoCuenta.setText("<html>¿No tienes cuenta? <span style='color:navy;'>Regístrate</span></html>\n");
        pnlLogin.add(jlNoCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 340, -1, 20));

        jlIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SimIcono.png"))); // NOI18N
        pnlLogin.add(jlIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 40, -1, 150));

        pnlContenedor.add(pnlLogin, "card2");

        pnlRegistro.setBackground(new java.awt.Color(255, 255, 255));
        pnlRegistro.setMaximumSize(new java.awt.Dimension(849, 490));
        pnlRegistro.setMinimumSize(new java.awt.Dimension(849, 525));
        pnlRegistro.setName(""); // NOI18N
        pnlRegistro.setPreferredSize(new java.awt.Dimension(849, 490));
        pnlRegistro.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlRegistroUsuario.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jlRegistroUsuario.setForeground(new java.awt.Color(0, 0, 102));
        jlRegistroUsuario.setText("Registro de Usuario");
        pnlRegistro.add(jlRegistroUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 40, -1, -1));

        txtRegCedula.setText("Cédula");
        pnlRegistro.add(txtRegCedula, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 80, 250, 40));

        txtRegNombre.setText("Nombre");
        txtRegNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegNombreActionPerformed(evt);
            }
        });
        pnlRegistro.add(txtRegNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 130, 250, 40));

        dtRegFecha.setBackground(new java.awt.Color(255, 255, 255));
        pnlRegistro.add(dtRegFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 180, 250, 40));

        txtRegCorreo.setText("Correo ");
        txtRegCorreo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegCorreoActionPerformed(evt);
            }
        });
        pnlRegistro.add(txtRegCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 310, 250, 40));

        rdbRegMasculino.setBackground(new java.awt.Color(255, 255, 255));
        btnGGenero.add(rdbRegMasculino);
        rdbRegMasculino.setText("Masculino");
        rdbRegMasculino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbRegMasculinoActionPerformed(evt);
            }
        });
        pnlRegistro.add(rdbRegMasculino, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 230, -1, -1));

        rdbRegFemenino.setBackground(new java.awt.Color(255, 255, 255));
        btnGGenero.add(rdbRegFemenino);
        rdbRegFemenino.setText("Femenino");
        pnlRegistro.add(rdbRegFemenino, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 230, -1, -1));
        pnlRegistro.add(pwpReg, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 260, 250, 40));

        btnCrearCuenta.setBackground(new java.awt.Color(0, 51, 102));
        btnCrearCuenta.setForeground(new java.awt.Color(255, 255, 255));
        btnCrearCuenta.setText("Crear Cuenta");
        pnlRegistro.add(btnCrearCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 360, 250, 35));

        jlGenero.setText("Género:");
        pnlRegistro.add(jlGenero, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 230, -1, -1));

        jlTieneCuenta.setText("<html>¿Ya tienes cuenta? <span style='color:navy;'>Inicia Sesión</span></html>");
        pnlRegistro.add(jlTieneCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 410, 190, 30));

        pnlContenedor.add(pnlRegistro, "card3");

        pnlInicio.add(pnlContenedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 450));

        tbpPrincipal.addTab("Inicio", pnlInicio);

        pnlEcosistemas.setBackground(new java.awt.Color(255, 255, 255));
        pnlEcosistemas.setMaximumSize(new java.awt.Dimension(849, 490));
        pnlEcosistemas.setMinimumSize(new java.awt.Dimension(849, 525));
        pnlEcosistemas.setPreferredSize(new java.awt.Dimension(849, 490));

        tblEcosistema.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblEcosistema);

        btnGenerarEcosistema.setBackground(new java.awt.Color(0, 102, 51));
        btnGenerarEcosistema.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerarEcosistema.setText("Generar ecosistema");
        btnGenerarEcosistema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarEcosistemaActionPerformed(evt);
            }
        });

        btnIniciar.setBackground(new java.awt.Color(0, 0, 102));
        btnIniciar.setForeground(new java.awt.Color(255, 255, 255));
        btnIniciar.setText("Iniciar");
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });

        btnPausar.setBackground(new java.awt.Color(204, 0, 0));
        btnPausar.setForeground(new java.awt.Color(255, 255, 255));
        btnPausar.setText("Pausar");
        btnPausar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPausarActionPerformed(evt);
            }
        });

        cmbEscenario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbEscenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEscenarioActionPerformed(evt);
            }
        });

        rbtnTerceraEspecie.setText("¿Introducir tercera especie?");
        rbtnTerceraEspecie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnTerceraEspecieActionPerformed(evt);
            }
        });

        pnlTerceraEspecie.setBackground(new java.awt.Color(255, 255, 255));

        btngTerceraEspecie.add(rbtnEspecieMutante);
        rbtnEspecieMutante.setText("Mutante");

        btngTerceraEspecie.add(rbtnEspecieAliadaPresas);
        rbtnEspecieAliadaPresas.setText("Aliada de presas");
        rbtnEspecieAliadaPresas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnEspecieAliadaPresasActionPerformed(evt);
            }
        });

        btngTerceraEspecie.add(rbtnEspecieAliadaDepredadores);
        rbtnEspecieAliadaDepredadores.setText("Aliada de depredadores");

        javax.swing.GroupLayout pnlTerceraEspecieLayout = new javax.swing.GroupLayout(pnlTerceraEspecie);
        pnlTerceraEspecie.setLayout(pnlTerceraEspecieLayout);
        pnlTerceraEspecieLayout.setHorizontalGroup(
            pnlTerceraEspecieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTerceraEspecieLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rbtnEspecieMutante, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbtnEspecieAliadaDepredadores)
                .addGap(24, 24, 24)
                .addComponent(rbtnEspecieAliadaPresas)
                .addGap(94, 94, 94))
        );
        pnlTerceraEspecieLayout.setVerticalGroup(
            pnlTerceraEspecieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTerceraEspecieLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlTerceraEspecieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtnEspecieMutante)
                    .addComponent(rbtnEspecieAliadaDepredadores)
                    .addComponent(rbtnEspecieAliadaPresas))
                .addContainerGap())
        );

        btnCerrarSesion.setBackground(new java.awt.Color(0, 51, 102));
        btnCerrarSesion.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrarSesion.setText("Cerrar Sesión");
        btnCerrarSesion.setMaximumSize(new java.awt.Dimension(72, 23));
        btnCerrarSesion.setMinimumSize(new java.awt.Dimension(72, 23));
        btnCerrarSesion.setPreferredSize(new java.awt.Dimension(72, 23));
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });

        jlSeleccionarEscenario.setText("Seleccione un escenario:");

        txtMovimientos.setColumns(20);
        txtMovimientos.setRows(5);
        jScrollPane2.setViewportView(txtMovimientos);

        cmbMutacion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Furia (depredadores)", "Veneno (presas)" }));

        chkMutacionesGeneticas.setText("Activar mutaciones genéticas");
        chkMutacionesGeneticas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMutacionesGeneticasActionPerformed(evt);
            }
        });

        jlSimuladorEcosistemas.setFont(new java.awt.Font("Nirmala UI", 1, 24)); // NOI18N
        jlSimuladorEcosistemas.setForeground(new java.awt.Color(0, 51, 102));
        jlSimuladorEcosistemas.setText("Simulador de Ecosistema");

        jlMatriz10x10.setFont(new java.awt.Font("Nirmala UI", 1, 14)); // NOI18N
        jlMatriz10x10.setForeground(new java.awt.Color(0, 51, 102));
        jlMatriz10x10.setText("Matríz 10x10");

        jlControles.setFont(new java.awt.Font("Nirmala UI", 1, 14)); // NOI18N
        jlControles.setForeground(new java.awt.Color(0, 51, 102));
        jlControles.setText("Controles");

        jlConsolaEventos.setFont(new java.awt.Font("Nirmala UI", 1, 14)); // NOI18N
        jlConsolaEventos.setForeground(new java.awt.Color(0, 51, 102));
        jlConsolaEventos.setText("Consola de eventos");

        jlExtensiones.setText("Extensiones:");

        javax.swing.GroupLayout pnlEcosistemasLayout = new javax.swing.GroupLayout(pnlEcosistemas);
        pnlEcosistemas.setLayout(pnlEcosistemasLayout);
        pnlEcosistemasLayout.setHorizontalGroup(
            pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlMatriz10x10)
                    .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rbtnTerceraEspecie, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                                        .addComponent(jlSeleccionarEscenario)
                                        .addGap(18, 18, 18)
                                        .addComponent(cmbEscenario, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jlConsolaEventos)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlControles)
                                    .addComponent(jlExtensiones)
                                    .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                                                .addGap(48, 48, 48)
                                                .addComponent(btnGenerarEcosistema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addComponent(chkMutacionesGeneticas, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(cmbMutacion, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                                                .addComponent(btnPausar)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(pnlTerceraEspecie, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(28, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEcosistemasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlSimuladorEcosistemas, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(171, 171, 171)
                .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );
        pnlEcosistemasLayout.setVerticalGroup(
            pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEcosistemasLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlSimuladorEcosistemas, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(jlMatriz10x10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                        .addComponent(jlControles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlSeleccionarEscenario)
                            .addComponent(cmbEscenario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlExtensiones)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rbtnTerceraEspecie)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlTerceraEspecie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkMutacionesGeneticas)
                            .addComponent(cmbMutacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGenerarEcosistema, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnPausar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlConsolaEventos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33))
        );

        tbpPrincipal.addTab("Ecosistemas", pnlEcosistemas);

        pnlReporte.setBackground(new java.awt.Color(255, 255, 255));
        pnlReporte.setMaximumSize(new java.awt.Dimension(849, 490));
        pnlReporte.setMinimumSize(new java.awt.Dimension(849, 525));

        lblTurnos.setText("Cantidad de turnos ejecutados:");

        pnlGraficoPresasDepredadores.setMinimumSize(new java.awt.Dimension(450, 240));
        pnlGraficoPresasDepredadores.setPreferredSize(new java.awt.Dimension(450, 240));

        javax.swing.GroupLayout pnlGraficoPresasDepredadoresLayout = new javax.swing.GroupLayout(pnlGraficoPresasDepredadores);
        pnlGraficoPresasDepredadores.setLayout(pnlGraficoPresasDepredadoresLayout);
        pnlGraficoPresasDepredadoresLayout.setHorizontalGroup(
            pnlGraficoPresasDepredadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlGraficoPresasDepredadoresLayout.setVerticalGroup(
            pnlGraficoPresasDepredadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );

        lblGraficoPresasDepredadores.setText("Gráfico De Presas y Depredadores");

        lblPrimeraExtincion.setText("presas extintas en el turno:");

        txtExtincion1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtExtincion1ActionPerformed(evt);
            }
        });

        lblSegundaExtincion.setText("Depredadores extintos en el turno:");

        pnlGraficoOcupacion.setMinimumSize(new java.awt.Dimension(450, 240));
        pnlGraficoOcupacion.setPreferredSize(new java.awt.Dimension(450, 240));

        javax.swing.GroupLayout pnlGraficoOcupacionLayout = new javax.swing.GroupLayout(pnlGraficoOcupacion);
        pnlGraficoOcupacion.setLayout(pnlGraficoOcupacionLayout);
        pnlGraficoOcupacionLayout.setHorizontalGroup(
            pnlGraficoOcupacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlGraficoOcupacionLayout.setVerticalGroup(
            pnlGraficoOcupacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel2.setText("Gráfico de ocupación del ecosistema");

        javax.swing.GroupLayout pnlReporteLayout = new javax.swing.GroupLayout(pnlReporte);
        pnlReporte.setLayout(pnlReporteLayout);
        pnlReporteLayout.setHorizontalGroup(
            pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReporteLayout.createSequentialGroup()
                .addGroup(pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlReporteLayout.createSequentialGroup()
                        .addGap(338, 338, 338)
                        .addGroup(pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlGraficoOcupacion, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlReporteLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlReporteLayout.createSequentialGroup()
                                .addComponent(lblTurnos, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnCantidadTurnos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlReporteLayout.createSequentialGroup()
                                .addGroup(pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblPrimeraExtincion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblSegundaExtincion, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                                .addGroup(pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlReporteLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(txtExtincion2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlReporteLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(txtExtincion1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(53, 53, 53)
                        .addGroup(pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblGraficoPresasDepredadores, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlGraficoPresasDepredadores, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(209, 343, Short.MAX_VALUE))
        );
        pnlReporteLayout.setVerticalGroup(
            pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReporteLayout.createSequentialGroup()
                .addGroup(pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlReporteLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTurnos, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spnCantidadTurnos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addGroup(pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPrimeraExtincion)
                            .addComponent(txtExtincion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlReporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSegundaExtincion)
                            .addComponent(txtExtincion2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlReporteLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblGraficoPresasDepredadores)
                        .addGap(18, 18, 18)
                        .addComponent(pnlGraficoPresasDepredadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(3, 3, 3)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(pnlGraficoOcupacion, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbpPrincipal.addTab("Reporte", pnlReporte);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(tbpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 1057, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tbpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCedulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCedulaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCedulaActionPerformed

    private void btnIngresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIngresarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnIngresarActionPerformed

    private void txtRegNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRegNombreActionPerformed

    private void txtRegCorreoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegCorreoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRegCorreoActionPerformed

    private void pwdLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pwdLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pwdLoginActionPerformed

    private void rdbRegMasculinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbRegMasculinoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdbRegMasculinoActionPerformed

    private void btnGenerarEcosistemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarEcosistemaActionPerformed
        // TODO add your handling code here
        ecosistemaController.generarEscenarioDesdeVista(); // por ahora 10 presas, 10 depredadores
    }//GEN-LAST:event_btnGenerarEcosistemaActionPerformed

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
        // TODO add your handling code here:
        ecosistemaController.iniciarSimulacion();
    }//GEN-LAST:event_btnIniciarActionPerformed

    private void btnPausarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPausarActionPerformed
        // TODO add your handling code here:
        ecosistemaController.pausarSimulacion();
    }//GEN-LAST:event_btnPausarActionPerformed

    private void cmbEscenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEscenarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbEscenarioActionPerformed

    private void rbtnEspecieAliadaPresasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnEspecieAliadaPresasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbtnEspecieAliadaPresasActionPerformed

    private void rbtnTerceraEspecieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnTerceraEspecieActionPerformed
        // TODO add your handling code here:
        pnlTerceraEspecie.setVisible(rbtnTerceraEspecie.isSelected());
    }//GEN-LAST:event_rbtnTerceraEspecieActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void chkMutacionesGeneticasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMutacionesGeneticasActionPerformed
        // TODO add your handling code here:
        cmbMutacion.setEnabled(chkMutacionesGeneticas.isSelected());
    }//GEN-LAST:event_chkMutacionesGeneticasActionPerformed

    private void txtExtincion1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtExtincion1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtExtincion1ActionPerformed
    

  
    
    

    public boolean isTerceraEspecieActiva() {
        return rbtnTerceraEspecie.isSelected();
    }

    public String getOpcionTerceraEspecie() {
        if (rbtnEspecieMutante.isSelected()) {
            return "Mutante";
        } else if (rbtnEspecieAliadaPresas.isSelected()) {
            return "AliadaPresas";
        } else if (rbtnEspecieAliadaDepredadores.isSelected()) {
            return "AliadaDepredadores";
        }
        return null; // ninguna
    }

    public javax.swing.JTextArea getTxtMovimientos() {
        return txtMovimientos;
    }

    public boolean isMutacionesActivadas() {
        return chkMutacionesGeneticas.isSelected();
    }

    public String getTipoMutacionSeleccionado() {
        if (!chkMutacionesGeneticas.isSelected()) {
            return null;
        }
        String texto = (String) cmbMutacion.getSelectedItem();
        if (texto == null) {
            return null;
        }

        if (texto.startsWith("Furia")) {
            return "FURIA";
        } else if (texto.startsWith("Veneno")) {
            return "VENENO";
        }
        return null;
    }

    private void dibujarGraficoPresasDepredadores(ReporteDatos datos) {
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

            ChartPanel panel = new ChartPanel(chart);

            pnlGraficoPresasDepredadores.removeAll();
            pnlGraficoPresasDepredadores.add(panel, BorderLayout.CENTER);
            pnlGraficoPresasDepredadores.validate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al dibujar gráfico Presas-Depredadores: " + ex.getMessage());
        }
    }

    private void dibujarGraficoOcupacion(ReporteDatos datos) {
        try {
            DefaultPieDataset dataset = new DefaultPieDataset();

            int ocupadas = datos.getCeldasOcupadas();
            int libres = datos.getTotalCeldas() - ocupadas;

            dataset.setValue("Ocupadas", (Number) ocupadas);
            dataset.setValue("Libres", (Number) libres);

            JFreeChart chart = ChartFactory.createPieChart(
                    "Ocupación del Ecosistema",
                    dataset,
                    true,
                    true,
                    false
            );

            ChartPanel panel = new ChartPanel(chart);

            pnlGraficoOcupacion.removeAll();
            pnlGraficoOcupacion.add(panel, BorderLayout.CENTER);
            pnlGraficoOcupacion.validate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al dibujar gráfico de ocupación: " + ex.getMessage());
        }
    }

    private void enviarPdfPorCorreo(String rutaPdf) {
        if (AuthController.usuarioActual == null) {
            return;
        }

        String destinatario = AuthController.usuarioActual.getCorreo();

        if (destinatario == null || destinatario.isEmpty()) {
            JOptionPane.showMessageDialog(this,
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
            JOptionPane.showMessageDialog(this,
                    "Error al enviar correo: " + ex.getMessage());
        }
    }
    
    
    
    
    

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnCrearCuenta;
    private javax.swing.ButtonGroup btnGGenero;
    private javax.swing.JButton btnGenerarEcosistema;
    private javax.swing.JButton btnIngresar;
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnPausar;
    private javax.swing.ButtonGroup btngTerceraEspecie;
    private javax.swing.JCheckBox chkMutacionesGeneticas;
    private javax.swing.JComboBox<String> cmbEscenario;
    private javax.swing.JComboBox<String> cmbMutacion;
    private com.toedter.calendar.JDateChooser dtRegFecha;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jlConsolaEventos;
    private javax.swing.JLabel jlControles;
    private javax.swing.JLabel jlExtensiones;
    private javax.swing.JLabel jlGenero;
    private javax.swing.JLabel jlIcono;
    private javax.swing.JLabel jlInicioSesion;
    private javax.swing.JLabel jlMatriz10x10;
    private javax.swing.JLabel jlNoCuenta;
    private javax.swing.JLabel jlRegistroUsuario;
    private javax.swing.JLabel jlSeleccionarEscenario;
    private javax.swing.JLabel jlSimuladorEcosistemas;
    private javax.swing.JLabel jlTieneCuenta;
    private javax.swing.JLabel lblGraficoPresasDepredadores;
    private javax.swing.JLabel lblPrimeraExtincion;
    private javax.swing.JLabel lblSegundaExtincion;
    private javax.swing.JLabel lblTurnos;
    private javax.swing.JPanel pnlContenedor;
    private javax.swing.JPanel pnlEcosistemas;
    private javax.swing.JPanel pnlGraficoOcupacion;
    private javax.swing.JPanel pnlGraficoPresasDepredadores;
    private javax.swing.JPanel pnlInicio;
    private javax.swing.JPanel pnlLogin;
    private javax.swing.JPanel pnlRegistro;
    private javax.swing.JPanel pnlReporte;
    private javax.swing.JPanel pnlTerceraEspecie;
    private javax.swing.JPasswordField pwdLogin;
    private javax.swing.JPasswordField pwpReg;
    private javax.swing.JRadioButton rbtnEspecieAliadaDepredadores;
    private javax.swing.JRadioButton rbtnEspecieAliadaPresas;
    private javax.swing.JRadioButton rbtnEspecieMutante;
    private javax.swing.JRadioButton rbtnTerceraEspecie;
    private javax.swing.JRadioButton rdbRegFemenino;
    private javax.swing.JRadioButton rdbRegMasculino;
    private javax.swing.JSpinner spnCantidadTurnos;
    private javax.swing.JTable tblEcosistema;
    private javax.swing.JTabbedPane tbpPrincipal;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtExtincion1;
    private javax.swing.JTextField txtExtincion2;
    private javax.swing.JTextArea txtMovimientos;
    private javax.swing.JTextField txtRegCedula;
    private javax.swing.JTextField txtRegCorreo;
    private javax.swing.JTextField txtRegNombre;
    // End of variables declaration//GEN-END:variables
}
