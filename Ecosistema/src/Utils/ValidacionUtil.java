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
    
    
    
    
    
    
    //Validamos la tercera especie: devuelve true si está activa y se seleccionó al menos una variante:
    public static boolean terceraEspecieValida(
            boolean terceraEspecieActiva, boolean mutante, boolean aliadasPresas, boolean aliadosDepredadores) {
        if (!terceraEspecieActiva) return true; // No está activa, no importa
        return mutante || aliadasPresas || aliadosDepredadores;
    }

    //Validamos la mutación: devuelve true si está activada y se seleccionó un tipo:
    public static boolean mutacionValida(boolean mutacionesActivas, String tipoMutacion) {
        if (!mutacionesActivas) return true; // No está activa, no importa
        return tipoMutacion != null && !tipoMutacion.trim().isEmpty();
    }

    //Validamos que se haya seleccionado un escenario base:
    public static boolean escenarioSeleccionado(Object escenario) {
        return escenario != null && !escenario.toString().trim().isEmpty()
                && !escenario.toString().equals("Seleccione un escenario");
    }
}
