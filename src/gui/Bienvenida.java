package gui;
import javax.swing.*;

public class Bienvenida extends JWindow {
    
    public Bienvenida() {
        setSize(330, 320);
        setLocationRelativeTo(null);

        Utilidades.cargarContenidoBienvenida(this);
    }
}