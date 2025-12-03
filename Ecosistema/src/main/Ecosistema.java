
package main;

import Controller.AuthController;
import Business.AuthService;
import View.frmPrincipal;
import com.formdev.flatlaf.FlatLightLaf;


public class Ecosistema {
    public static void main(String[] args) {

            //IMportacion de librería FlatLaf, para interfaz moderna
            //en los tabs. 
            //Activamos FlatLaf
            FlatLightLaf.setup(); // más recomendado que UIManager.setLookAndFeel

            // Crear la vista
            frmPrincipal vista = new frmPrincipal();
            // Crear el servicio (modelo)
            AuthService service = new AuthService();
            // Crear el controlador y enlazarlo con la vista y el modelo
            AuthController controller = new AuthController(vista, service);

            // Mostrar la vista
            vista.setVisible(true);
        }
   
    
}
