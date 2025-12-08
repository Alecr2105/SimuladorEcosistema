package Business;

/*Clase encargada de la autenticación y el registro
Encripta la contraseña para su respectivo almacenamiento en usuarios.txt
comparar login contra el archivo
registrar usuario usando UsuarioDAO
*/

import Data.UsuarioDAO;
import Model.Usuario;
import Utils.EmailService;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.JOptionPane;



public class AuthService {
    private final UsuarioDAO userDAO = new UsuarioDAO();
    private final EmailService emailService = new EmailService();

    
    
    
    //MÉTODOS PRINCIPALES:
    //********************************************************************************
    //LOGIN: Validar cédula y encriptar contraseña: 
    public boolean iniciarSesion(int cedula, String contrasena){
        //Buscar usuario:
        Usuario found = userDAO.buscarPorCedula(cedula);
        if(found == null){
            JOptionPane.showMessageDialog(null, "El usuario con la cédula " + cedula +
            " no existe en el sistema", "Error",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        //Encriptar la contraseña ingresada:
        String contrasenaEncrip = encriptar(contrasena);
        
        //Comparar hash guardado vs hash recién generado:
        if(found.getContrasena().equals(contrasenaEncrip)){
            enviarCorreoInicioSesion(found);
            return true;
        }else{
            return false; 
        }
   }
    
    
    
    public boolean registrarUsuario(Usuario nuevo){
        //Validamos existencia de usuario con ese ID:
        Usuario exists = userDAO.buscarPorCedula(nuevo.getCedula());
        if(exists != null){
            JOptionPane.showMessageDialog(null, "Usuario ya existente con ese número de cédula",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false; //User exist
        }
        //Encriptamos contraseña antes de guardar en archivo:
        String passEnc = encriptar(nuevo.getContrasena());
        nuevo.setContrasena(passEnc);
        
        //Guardamos en archivo:
        userDAO.guardarUsuario(nuevo);
        JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente.");
        enviarCorreoRegistro(nuevo);
        return true;
    }
    
    public Usuario buscarPorCedula(int cedula) {
        return userDAO.buscarPorCedula(cedula);
    }
    
    
    
    
    
    //Encrypt the entered password(SHA-256):
    //**************************************
    public String encriptar(String contr){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(contr.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for(byte b : hash){
                sb.append(String.format("%02x", b));
            }
            return  sb.toString();//Send to file users.txt
        }catch(NoSuchAlgorithmException e){
            throw  new RuntimeException("Error al encriptar la contraseña ingresada: " + e.getMessage());
        }
    }
    
    
    
    
    
    //Notificaciones por correo:
    //**************************
    private void enviarCorreoRegistro(Usuario usuario) {
        try {
            String asunto = "Cuenta creada en EcoSim";
            String mensaje = "Su cuenta se ha creado exitosamente.";
            emailService.enviarCorreo(usuario.getCorreo(), asunto, mensaje);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null, "No se pudo enviar el correo de registro: " + e.getMessage());
        }
    }

    private void enviarCorreoInicioSesion(Usuario usuario) {
        try {
            String asunto = "Inicio de sesión en EcoSim";
            String mensaje = "Se ha iniciado sesión en su cuenta de EcoSim.";
            emailService.enviarCorreo(usuario.getCorreo(), asunto, mensaje);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null, "No se pudo enviar el correo de inicio de sesión: " + e.getMessage());
        }
    }
    
    public void enviarCorreoCierreSesion(Usuario usuario) {
        if (usuario == null) {
            return;
        }

        try {
            String asunto = "Cierre de sesión en EcoSim";
            String mensaje = "Se ha cerrado sesión en su cuenta de EcoSim.";
            emailService.enviarCorreo(usuario.getCorreo(), asunto, mensaje);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null, "No se pudo enviar el correo de cierre de sesión: " + e.getMessage());
        }
    }
}
