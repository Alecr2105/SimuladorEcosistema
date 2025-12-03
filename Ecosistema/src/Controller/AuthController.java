package Controller;

/*Esta clase se encarga de recibir datos del FrmPrincipal
Pedir a AuthService que valide 
Si login correcto -> abrir FrmPrincipal*/

import Model.Usuario;
import Business.AuthService;
import Utils.ValidacionUtil;
import View.frmPrincipal;
import javax.swing.JOptionPane;
import java.util.Date;

public class AuthController {

    private frmPrincipal view;
    private AuthService service;

    public static boolean sesionIniciada = false;

    public AuthController(frmPrincipal view, AuthService service) {

        this.view = view;
        this.service = service;

        // Cambiar pantallas
        this.view.getLblNoCuenta().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                view.mostrarRegistro();
            }
        });

        this.view.getLblTieneCuenta().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                view.mostrarLogin();
            }
        });

        // Botones
        this.view.getBtnRegistrar().addActionListener(e -> registrarUsuario());
        this.view.getBtnIniciar().addActionListener(e -> iniciarSesion());
    }

    
    // VALIDACIÓN DE REGISTRO
    private boolean validarRegistro() {
        //Cédula:
        if (!ValidacionUtil.cedulaValida(view.getTxtRegCedula().getText())) {
            JOptionPane.showMessageDialog(view, "La cédula debe tener entre 9 y 12 dígitos.");
            return false;
        }
        
        //Contraseña:
        String pass = String.valueOf(view.getPwdReg().getPassword());
        if (!ValidacionUtil.contrasenaValida(pass)) {
            JOptionPane.showMessageDialog(view, "La contraseña debe tener al menos 6 caracteres.");
            return false;
        }
        
        //Nombre:
        if (!ValidacionUtil.nombreValido(view.getTxtRegNombre().getText())) {
            JOptionPane.showMessageDialog(view, "El nombre solo debe contener letras.");
            return false;
        }
        
        //Correo:
        if (!ValidacionUtil.correoValido(view.getTxtRegCorreo().getText())) {
            JOptionPane.showMessageDialog(view, "El correo es inválido.");
            return false;
        }
        
        //Fecha de nacimiento:
        Date fecha = view.getDtRegFecha().getDate();
        if (fecha == null) {
            JOptionPane.showMessageDialog(view, "Seleccione la fecha de nacimiento.");
            return false;
        }
        
        //Validar que la fecha no sea futura:
        if (!ValidacionUtil.fechaValida(fecha)) {
            JOptionPane.showMessageDialog(view, "La fecha no puede ser futura.");
            return false;
        }
        
        //Validar la mayoría de edad:
        if(!ValidacionUtil.mayorDeEdad(fecha)){
            JOptionPane.showMessageDialog(view, "Debe ser mayor de 18 años para registrarse.");
            return false;
        }
        
        
        
        
        //Género:
        if (!view.getRdbHombre().isSelected() && !view.getRdbMujer().isSelected()) {
            JOptionPane.showMessageDialog(view, "Seleccione un género.");
            return false;
        }
        return true;
    }

    
    
    
    
    
    
    //REGISTRO:
    private void registrarUsuario() {

        if (!validarRegistro()) return;

        try {
            Date fecha = view.getDtRegFecha().getDate();

            String genero = view.getRdbHombre().isSelected() ? "Hombre" : "Mujer";

            Usuario u = new Usuario(
                    Integer.parseInt(view.getTxtRegCedula().getText()),
                    view.getTxtRegNombre().getText(),
                    fecha,
                    genero,
                    String.valueOf(view.getPwdReg().getPassword()),
                    view.getTxtRegCorreo().getText()
            );

            service.registrarUsuario(u);

            view.limpiarCampos();
            view.mostrarLogin();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error registrando usuario: " + e.getMessage());
        }
    }
    
    
    // LOGIN
    private void iniciarSesion() {

        String ced = view.getTxtCedula().getText();
        String pwd = String.valueOf(view.getPwdContrasena().getPassword());

        if (ced.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Debe ingresar cédula y contraseña.");
            return;
        }

        try {

            boolean ok = service.iniciarSesion(Integer.parseInt(ced), pwd);

            if (ok) {
                sesionIniciada = true;
                JOptionPane.showMessageDialog(view, "Bienvenido!");

                view.getTbpPrincipal().setSelectedComponent(view.getPnlEcosistemas());
                view.bloquearAccesoInicio(); //Bloqueamos el tab de inicio
            } else {
                JOptionPane.showMessageDialog(view, "Credenciales incorrectas.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error al iniciar sesión: " + e.getMessage());
        }
    }
}
