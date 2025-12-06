package Utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class ValidacionUtil {
    //Cédula: solo números, entre 9 y 12 dígitos
    public static boolean cedulaValida(String cedula) {
        return cedula != null && cedula.matches("\\d{9,12}");
    }
    
    //Contraseña: mínimo 6 caracteres
    public static boolean contrasenaValida(String password) {
        return password != null && password.length() >= 6;
    }
    
    //Nombre: solo letras y espacios
    public static boolean nombreValido(String nombre) {
        return nombre != null && nombre.matches("[a-zA-ZñÑáéíóúÁÉÍÓÚ ]+");
    }
    
    //Correo: formato válido
    public static boolean correoValido(String correo) {
        return correo != null && correo.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    //Fecha válida y no futura
    public static boolean fechaValida(java.util.Date fecha) {
        try {
            LocalDate fechaNac = fecha.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            return !fechaNac.isAfter(LocalDate.now()); 
        } catch (Exception e) {
            return false;
        }
    }
    
    
    public static boolean mayorDeEdad(Date fecha){
        if (fecha == null) return false;
        
        LocalDate nacimiento = fecha.toInstant().
                atZone(ZoneId.systemDefault()).
                toLocalDate();
        LocalDate hoy = LocalDate.now();
        
        int edad = Period.between(nacimiento, hoy).getYears();
       
        return edad >= 18;
    }
    
    
    
    
    
    
    //Validaciones en ECOSISTEMAS:
    public static boolean validarSelecciones(Object escenario, boolean terceraEspecieActiva,
        Object  varianteTercera, boolean mutacionesActivas, Object tipoMutacion){
        
        if(escenario == null || escenario.toString().trim().isEmpty()) return  false;
        
        if(terceraEspecieActiva) {
            if(varianteTercera == null || varianteTercera.toString().trim().isEmpty()) return false;
        }
        if(mutacionesActivas) {
            if(tipoMutacion == null || tipoMutacion.toString().trim().isEmpty()){
                return false;
            }
        }
        return true;
    }
}
