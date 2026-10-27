package inicioAPP;
import dominio.Sistema;
import gui.Bienvenida;
import gui.Menu;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Aspecto de Windows (o del sistema operativo que sea) en lugar del aspecto por defecto de Java
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}   // si no se puede, queda el aspecto por defecto

        Bienvenida inicio = new Bienvenida();
        inicio.setVisible(true);
        
        try {
            Thread.sleep(3000); // Espera 3 segundos
        } catch (InterruptedException ex) {}
        
        inicio.dispose();
        
        String[] opciones = {"Sistema Nuevo", "Cargar Datos Previos", "Datos Ficticios"};
        int opcion = JOptionPane.showOptionDialog(null, "Cómo desea iniciar?", "Inicio", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones);     // showOptionDialog para personalizar la ventana opciones
         
        if (opcion == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }

        Sistema sistema = new Sistema();
        
        try {
            if (opcion == 1) {
                File guardado = new File("sistema.ser");
                if (guardado.exists()) {
                    sistema = Sistema.cargarSistema();
                    if (sistema.getListaAreas().isEmpty() && sistema.getListaManagers().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El archivo se cargó, pero el sistema está vacío (sin datos)");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No hay datos previos, se inicia vacío.");
                }
            } else if (opcion == 2) {
                sistema.cargarDatosFicticios();
            }           
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error cargando: " + ex.getMessage());
        }
        
        new Menu(sistema).setVisible(true);
    }
}