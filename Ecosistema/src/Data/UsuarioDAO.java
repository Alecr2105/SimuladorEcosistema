package Data;

/*Esta clase se encarga de leer/escribir usuarios al archivo usuarios.txt
hace: Guarda un usuario nuevo
Valida la existencia por cédula
Busca un usuario por cédula
cargar todos los usuarios registrados
Guardar contraseña encriptada (hash)*/

import Model.Usuario;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import javax.swing.JOptionPane;

//CRUD and access to file users.txt. 
public class UsuarioDAO {
    private final String ARCHIVO = "usuarios.txt";
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
   
    //Convert user to line:
    private String toFileString(Usuario u){
        return u.getCedula() + ";" +
               u.getNombre() + ";" +
               sdf.format(u.getFechaNacimiento()) + ";" +
               u.getGenero() + ";" +
               u.getContrasena() + ";" +
               u.getCorreo();
    }
    //Convert line to user:
    private Usuario fromFileString(String line){
        try{
            String[] p = line.split(";");
            return new Usuario(
                    Integer.parseInt(p[0]),   //Cédula
                    p[1],                     //Nombre
                    sdf.parse(p[2]),          //Fecha de nacimiento
                    p[3],                     //Género
                    p[4],                     //Constraseña
                    p[5]                      //Correo
            );
        }catch(ParseException e){
            JOptionPane.showMessageDialog(null, "Error al parsear la fecha de nacimiento",
            "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    
    
    
    
    //Load all users to the file:
    public List<Usuario> cargarUsuarios(){
        List<Usuario> list = new ArrayList<>();
        
        File file = new File(ARCHIVO);
        if(!file.exists()){
            return list; //If the list is empy
        }
        try(BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))){
            String line;
            while((line = br.readLine()) != null){
                Usuario u = fromFileString(line);
                if(u != null){
                    list.add(u);
                }
            }
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, "Error al cargar el archivo",
            "Error", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }
    
    
    
    
    
    //Save a new user to the file:
    public void guardarUsuario(Usuario u){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            bw.write(toFileString(u));
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
            "Error al guardar usuario",
            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    
    
    //Search for user by ID number:
    public Usuario buscarPorCedula(int cedula){
        for(Usuario u : cargarUsuarios()){
            if(u.getCedula() == cedula){
                return u;
            }
        }
        return null;
    }
}
