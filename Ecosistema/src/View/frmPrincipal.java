package View;

import javax.swing.JOptionPane;
import Controller.AuthController;
import Controller.EcosistemaController;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class frmPrincipal extends javax.swing.JFrame {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(frmPrincipal.class.getName());
    private EcosistemaController ecosistemaController;

    public frmPrincipal() {
        initComponents(); 
        setLocationRelativeTo(null);//centramos pantalla:
        cmbEscenario.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[]{"Equilibrado", "Depredadores dominan", "Presas dominan"}
        ));

        //Personalizacion de tabs con FlatLaf
        tbpPrincipal.putClientProperty("JTabbedPane.tabHeight", 40);
        tbpPrincipal.putClientProperty("JTabbedPane.showTabSeparators", true);
        tbpPrincipal.putClientProperty("JTabbedPane.tabSeparatorsFullHeight", true);
        tbpPrincipal.putClientProperty("JTabbedPane.tabAreaAlignment", "center");
        tbpPrincipal.putClientProperty("JTabbedPane.tabInsets", new java.awt.Insets(6, 20, 6, 20));
        pnlTerceraEspecie.setVisible(false);
        
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
    public void desbloquearAccesoInicio(){
        tbpPrincipal.setEnabledAt(0, true);
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

    // LOGIN (pnlLogin)
    public javax.swing.JTextField getTxtCedula() {
        return txtCedula;
    }

    public javax.swing.JPasswordField getPwdContrasena() {
        return pwdLogin;
    }

    public javax.swing.JButton getBtnCerrarSesion(){
        return btnCerrarSesion;
    }
    
    public javax.swing.JButton getBtnIniciar() {
        return btnIngresar;
    }

    public javax.swing.JLabel getLblNoCuenta() {
        return jlNoCuenta;
    }

    // REGISTRO (txtRegNombre panel)
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
    public EcosistemaController getEcosistemaController(){
        return ecosistemaController;
    }
    
    
    
    
    

    //Métodos para mostrar LOGIN y REGISTRO:
    public void mostrarLogin() {
        ((java.awt.CardLayout) pnlContenedor.getLayout()).show(pnlContenedor, "card2");
    }

    public void mostrarRegistro() {
        ((java.awt.CardLayout) pnlContenedor.getLayout()).show(pnlContenedor, "card3");
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
        btnGenerarEscenario = new javax.swing.JButton();
        btnSiguienteTurno = new javax.swing.JButton();
        btnIniciar = new javax.swing.JButton();
        btnPausar = new javax.swing.JButton();
        cmbEscenario = new javax.swing.JComboBox<>();
        rbtnTerceraEspecie = new javax.swing.JRadioButton();
        pnlTerceraEspecie = new javax.swing.JPanel();
        rbtnEspecieMutante = new javax.swing.JRadioButton();
        rbtnEspecieAliadaPresas = new javax.swing.JRadioButton();
        rbtnEspecieAliadaDepredadores = new javax.swing.JRadioButton();
        btnCerrarSesion = new javax.swing.JButton();

        jCheckBox1.setText("jCheckBox1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximizedBounds(new java.awt.Rectangle(849, 490, 490, 490));
        setMinimumSize(new java.awt.Dimension(849, 525));

        tbpPrincipal.setBackground(new java.awt.Color(255, 255, 255));
        tbpPrincipal.setMaximumSize(new java.awt.Dimension(849, 490));
        tbpPrincipal.setMinimumSize(new java.awt.Dimension(849, 490));
        tbpPrincipal.setOpaque(true);
        tbpPrincipal.setPreferredSize(new java.awt.Dimension(849, 490));

        pnlInicio.setBackground(new java.awt.Color(255, 255, 255));
        pnlInicio.setForeground(new java.awt.Color(255, 255, 255));
        pnlInicio.setMaximumSize(new java.awt.Dimension(849, 490));
        pnlInicio.setMinimumSize(new java.awt.Dimension(849, 490));
        pnlInicio.setPreferredSize(new java.awt.Dimension(849, 490));
        pnlInicio.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlContenedor.setBackground(new java.awt.Color(255, 255, 255));
        pnlContenedor.setMaximumSize(new java.awt.Dimension(849, 490));
        pnlContenedor.setMinimumSize(new java.awt.Dimension(849, 490));
        pnlContenedor.setLayout(new java.awt.CardLayout());

        pnlLogin.setBackground(new java.awt.Color(255, 255, 255));
        pnlLogin.setMaximumSize(new java.awt.Dimension(849, 490));
        pnlLogin.setMinimumSize(new java.awt.Dimension(849, 490));
        pnlLogin.setPreferredSize(new java.awt.Dimension(849, 490));
        pnlLogin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlInicioSesion.setBackground(new java.awt.Color(255, 255, 255));
        jlInicioSesion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jlInicioSesion.setForeground(new java.awt.Color(0, 51, 102));
        jlInicioSesion.setText("Inicio Sesión");
        pnlLogin.add(jlInicioSesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 120, -1, -1));

        txtCedula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCedulaActionPerformed(evt);
            }
        });
        pnlLogin.add(txtCedula, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 170, 250, 40));

        pwdLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pwdLoginActionPerformed(evt);
            }
        });
        pnlLogin.add(pwdLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 220, 250, 40));

        btnIngresar.setBackground(new java.awt.Color(0, 51, 102));
        btnIngresar.setForeground(new java.awt.Color(255, 255, 255));
        btnIngresar.setText("Ingresar");
        btnIngresar.setOpaque(true);
        btnIngresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIngresarActionPerformed(evt);
            }
        });
        pnlLogin.add(btnIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 270, 250, 40));

        jlNoCuenta.setText("<html>¿No tienes cuenta? <span style='color:navy;'>Regístrate</span></html>\n");
        pnlLogin.add(jlNoCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 320, -1, 20));

        jlIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SimIcono.png"))); // NOI18N
        pnlLogin.add(jlIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, -1, 150));

        pnlContenedor.add(pnlLogin, "card2");

        pnlRegistro.setBackground(new java.awt.Color(255, 255, 255));
        pnlRegistro.setMaximumSize(new java.awt.Dimension(849, 490));
        pnlRegistro.setMinimumSize(new java.awt.Dimension(849, 490));
        pnlRegistro.setName(""); // NOI18N
        pnlRegistro.setPreferredSize(new java.awt.Dimension(849, 490));
        pnlRegistro.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlRegistroUsuario.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jlRegistroUsuario.setForeground(new java.awt.Color(0, 0, 102));
        jlRegistroUsuario.setText("Registro de Usuario");
        pnlRegistro.add(jlRegistroUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 30, -1, -1));

        txtRegCedula.setText("Cédula");
        pnlRegistro.add(txtRegCedula, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 70, 250, 40));

        txtRegNombre.setText("Nombre");
        txtRegNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegNombreActionPerformed(evt);
            }
        });
        pnlRegistro.add(txtRegNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 120, 250, 40));

        dtRegFecha.setBackground(new java.awt.Color(255, 255, 255));
        pnlRegistro.add(dtRegFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 170, 250, 40));

        txtRegCorreo.setText("Correo ");
        txtRegCorreo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegCorreoActionPerformed(evt);
            }
        });
        pnlRegistro.add(txtRegCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 300, 250, 40));

        rdbRegMasculino.setBackground(new java.awt.Color(255, 255, 255));
        btnGGenero.add(rdbRegMasculino);
        rdbRegMasculino.setText("Masculino");
        rdbRegMasculino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbRegMasculinoActionPerformed(evt);
            }
        });
        pnlRegistro.add(rdbRegMasculino, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 220, -1, -1));

        rdbRegFemenino.setBackground(new java.awt.Color(255, 255, 255));
        btnGGenero.add(rdbRegFemenino);
        rdbRegFemenino.setText("Femenino");
        pnlRegistro.add(rdbRegFemenino, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 220, -1, -1));
        pnlRegistro.add(pwpReg, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 250, 250, 40));

        btnCrearCuenta.setBackground(new java.awt.Color(0, 51, 102));
        btnCrearCuenta.setForeground(new java.awt.Color(255, 255, 255));
        btnCrearCuenta.setText("Crear Cuenta");
        pnlRegistro.add(btnCrearCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 350, 250, 35));

        jlGenero.setText("Género:");
        pnlRegistro.add(jlGenero, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 220, -1, -1));

        jlTieneCuenta.setText("<html>¿Ya tienes cuenta? <span style='color:navy;'>Inicia Sesión</span></html>");
        pnlRegistro.add(jlTieneCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 390, 190, 30));

        pnlContenedor.add(pnlRegistro, "card3");

        pnlInicio.add(pnlContenedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 450));

        tbpPrincipal.addTab("Inicio", pnlInicio);

        pnlEcosistemas.setBackground(new java.awt.Color(255, 255, 255));
        pnlEcosistemas.setMaximumSize(new java.awt.Dimension(849, 490));
        pnlEcosistemas.setMinimumSize(new java.awt.Dimension(849, 490));
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

        btnGenerarEscenario.setText("Generar");
        btnGenerarEscenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarEscenarioActionPerformed(evt);
            }
        });

        btnSiguienteTurno.setText("Siguiente Turno");
        btnSiguienteTurno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteTurnoActionPerformed(evt);
            }
        });

        btnIniciar.setText("Iniciar");
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });

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
                .addContainerGap()
                .addGroup(pnlTerceraEspecieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtnEspecieMutante, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtnEspecieAliadaPresas, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtnEspecieAliadaDepredadores, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(77, Short.MAX_VALUE))
        );
        pnlTerceraEspecieLayout.setVerticalGroup(
            pnlTerceraEspecieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTerceraEspecieLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtnEspecieMutante)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(rbtnEspecieAliadaPresas)
                .addGap(39, 39, 39)
                .addComponent(rbtnEspecieAliadaDepredadores)
                .addContainerGap())
        );

        btnCerrarSesion.setBackground(new java.awt.Color(0, 51, 102));
        btnCerrarSesion.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrarSesion.setText("Cerrar Sesión");
        btnCerrarSesion.setMaximumSize(new java.awt.Dimension(72, 23));
        btnCerrarSesion.setMinimumSize(new java.awt.Dimension(72, 23));
        btnCerrarSesion.setOpaque(true);
        btnCerrarSesion.setPreferredSize(new java.awt.Dimension(72, 23));
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlEcosistemasLayout = new javax.swing.GroupLayout(pnlEcosistemas);
        pnlEcosistemas.setLayout(pnlEcosistemasLayout);
        pnlEcosistemasLayout.setHorizontalGroup(
            pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                .addGap(161, 161, 161)
                .addComponent(btnSiguienteTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                        .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addComponent(btnGenerarEscenario, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                                .addGap(128, 128, 128)
                                .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbEscenario, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                                        .addComponent(btnIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnPausar, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(rbtnTerceraEspecie, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 72, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEcosistemasLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlTerceraEspecie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        pnlEcosistemasLayout.setVerticalGroup(
            pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEcosistemasLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSiguienteTurno)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEcosistemasLayout.createSequentialGroup()
                .addComponent(cmbEscenario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGenerarEscenario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEcosistemasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnIniciar)
                    .addComponent(btnPausar))
                .addGap(18, 18, 18)
                .addComponent(rbtnTerceraEspecie)
                .addGap(35, 35, 35)
                .addComponent(pnlTerceraEspecie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(97, 97, 97)
                .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );

        tbpPrincipal.addTab("Ecosistemas", pnlEcosistemas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(tbpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 843, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tbpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 485, Short.MAX_VALUE)
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

    private void btnGenerarEscenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarEscenarioActionPerformed
        // TODO add your handling code here
        ecosistemaController.generarEscenarioDesdeVista(); // por ahora 10 presas, 10 depredadores
    }//GEN-LAST:event_btnGenerarEscenarioActionPerformed

    private void btnSiguienteTurnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteTurnoActionPerformed
        // TODO add your handling code here:
        ecosistemaController.siguienteTurnoManual();
    }//GEN-LAST:event_btnSiguienteTurnoActionPerformed

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
    public javax.swing.JTable getTblEcosistema() {
        return tblEcosistema;
    }

    public javax.swing.JComboBox<String> getCmbEscenario() {
        return cmbEscenario;
    }

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

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnCrearCuenta;
    private javax.swing.ButtonGroup btnGGenero;
    private javax.swing.JButton btnGenerarEscenario;
    private javax.swing.JButton btnIngresar;
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnPausar;
    private javax.swing.JButton btnSiguienteTurno;
    private javax.swing.ButtonGroup btngTerceraEspecie;
    private javax.swing.JComboBox<String> cmbEscenario;
    private com.toedter.calendar.JDateChooser dtRegFecha;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jlGenero;
    private javax.swing.JLabel jlIcono;
    private javax.swing.JLabel jlInicioSesion;
    private javax.swing.JLabel jlNoCuenta;
    private javax.swing.JLabel jlRegistroUsuario;
    private javax.swing.JLabel jlTieneCuenta;
    private javax.swing.JPanel pnlContenedor;
    private javax.swing.JPanel pnlEcosistemas;
    private javax.swing.JPanel pnlInicio;
    private javax.swing.JPanel pnlLogin;
    private javax.swing.JPanel pnlRegistro;
    private javax.swing.JPanel pnlTerceraEspecie;
    private javax.swing.JPasswordField pwdLogin;
    private javax.swing.JPasswordField pwpReg;
    private javax.swing.JRadioButton rbtnEspecieAliadaDepredadores;
    private javax.swing.JRadioButton rbtnEspecieAliadaPresas;
    private javax.swing.JRadioButton rbtnEspecieMutante;
    private javax.swing.JRadioButton rbtnTerceraEspecie;
    private javax.swing.JRadioButton rdbRegFemenino;
    private javax.swing.JRadioButton rdbRegMasculino;
    private javax.swing.JTable tblEcosistema;
    private javax.swing.JTabbedPane tbpPrincipal;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtRegCedula;
    private javax.swing.JTextField txtRegCorreo;
    private javax.swing.JTextField txtRegNombre;
    // End of variables declaration//GEN-END:variables
}
