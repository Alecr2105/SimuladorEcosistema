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

        // Cambiar pantallas de incio sesión y registro:
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

        // Botones de autenticación:
        this.view.getBtnRegistrar().addActionListener(e -> registrarUsuario());
        this.view.getBtnIniciar().addActionListener(e -> iniciarSesion());
        this.view.getBtnCerrarSesion().addActionListener(e -> cerrarSesion());
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
            JOptionPane.showMessageDialog(view, "Personas menores de edad no pueden ingresar al sistema");
            return false;
        }
        
        //Género:
        if (!view.getRdbMaculino().isSelected() && !view.getRdbFemenino().isSelected()) {
            JOptionPane.showMessageDialog(view, "Seleccione un género.");
            return false;
        }
        return true;
    }

    
    
    
    
    
    //Operacion de autenticación:
    //***********************************************************************************
    //Registrar:
    private void registrarUsuario() {

        if (!validarRegistro()) return;

        try {
            Date fecha = view.getDtRegFecha().getDate();

            String genero = view.getRdbMaculino().isSelected() ? "Masculino" : "Femenino";

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
    
    
    //Iniciar Sesión:
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
                JOptionPane.showMessageDialog(view, "Bienvenido a EcoSim!");

                view.getTbpPrincipal().setSelectedComponent(view.getPnlEcosistemas());
                view.bloquearAccesoInicio(); //Bloqueamos el tab de inicio
            } else {
                JOptionPane.showMessageDialog(view, "Credenciales incorrectas.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error al iniciar sesión: " + e.getMessage());
        }
    }
    
    //Cerrar Sesión:
    private void cerrarSesion(){
        int opcion = JOptionPane.showConfirmDialog(view,
                "¿Desea cerrar la sesión?",
                "Confirmar cierre de sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if(opcion != JOptionPane.YES_OPTION){
            return; //Usuario canceló
        }
        
        //Detener simulación en proceso:
        if(view.getEcosistemaController() != null){
            view.getEcosistemaController().pausarSimulacion();
        }
        
        //Desbloquear tab de inicio:
        view.desbloquearAccesoInicio();
        
        //Volver al tab de inicio:
        view.getTbpPrincipal().setSelectedComponent(view.getPnlInicio());
        
        view.limpiarCampos();
        
        //Marcar sesion como cerrada:
        sesionIniciada = false;
        
        JOptionPane.showMessageDialog(view, "Sesión finalizada exitosamente.");
    }
}
