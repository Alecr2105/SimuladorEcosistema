
package main;

import Controller.AuthController;
import Business.AuthService;
import View.frmPrincipal;
import com.formdev.flatlaf.FlatLightLaf;


public class Ecosistema {
    public static void main(String[] args) {

            //IMportacion de librería FlatLaf, para interfaz moderna en los tabs. 
            //Activamos FlatLaf
            FlatLightLaf.setup();

            //Creamos la vista:
            frmPrincipal vista = new frmPrincipal();
            
            //Creamos el servicio modelo:
            AuthService service = new AuthService();
            
            //Creamos el controlador y lo enlasamos con la vista y el modelo:
            AuthController controller = new AuthController(vista, service);

            //Mostrar la vista:
            vista.setVisible(true);
        }
   
    
}
