package gui;

import javax.swing.*;
import java.awt.*;


public class Utilidades {

    public static void establecerIcono(Window ventana) {
        try {
            java.net.URL url = Utilidades.class.getResource("/recursos/loguitoEmpresa.png");
            
            Image imgOriginal = new ImageIcon(url).getImage();
            
            java.util.ArrayList<Image> listaIconos = new java.util.ArrayList<>();
            
            // 16x16 (Barra de título pequeña)
            listaIconos.add(imgOriginal.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
            // 32x32 (Barra de tareas / Alt-Tab)
            listaIconos.add(imgOriginal.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
            // 64x64 (Monitores grandes)
            listaIconos.add(imgOriginal.getScaledInstance(64, 64, Image.SCALE_SMOOTH));
            // 128x128 (Icono de aplicación grande)
            listaIconos.add(imgOriginal.getScaledInstance(128, 128, Image.SCALE_SMOOTH));
            
            ventana.setIconImages(listaIconos);
            
        } catch (Exception ex) {}
    }

    public static Icon cargarImagenEscalada(String ruta, int anchoDeseado, int altoDeseado) {
        try {
            java.net.URL url = Utilidades.class.getResource(ruta);
            if (url == null) return null;
            
            ImageIcon original = new ImageIcon(url);
            Image imgOriginal = original.getImage();

            return new IconoHighDPI(imgOriginal, anchoDeseado, altoDeseado);

        } catch (Exception ex) {
            return null;
        }
    }

    private static class IconoHighDPI implements Icon {
        private final Image imagen;
        private final int ancho;
        private final int alto;

        public IconoHighDPI(Image imagen, int ancho, int alto) {
            this.imagen = imagen;
            
            float ratio = (float) imagen.getWidth(null) / imagen.getHeight(null);
            int nuevoAncho = ancho;
            int nuevoAlto = (int) (ancho / ratio);

            if (nuevoAlto > alto) {
                nuevoAlto = alto;
                nuevoAncho = (int) (alto * ratio);
            }
            
            this.ancho = nuevoAncho;
            this.alto = nuevoAlto;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.drawImage(imagen, x, y, ancho, alto, null);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return ancho; }

        @Override
        public int getIconHeight() { return alto; }
    }
    
    public static void cargarLogoPrincipal(JFrame ventana) {
        establecerIcono(ventana);
        JLabel lblFondoLogo = new JLabel();
        lblFondoLogo.setHorizontalAlignment(SwingConstants.CENTER);
        
        Icon icono = cargarImagenEscalada("/recursos/logoEmpresa.png", 350, 350);
        
        if (icono != null) lblFondoLogo.setIcon(icono);
        else lblFondoLogo.setText("LOGO EMPRESA");

        ventana.add(lblFondoLogo, BorderLayout.CENTER);
    }

    public static void cargarContenidoBienvenida(JWindow ventana) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        
        Icon icono = cargarImagenEscalada("/recursos/logoERP.png", 275, 275);
        
        if (icono != null) lblLogo.setIcon(icono);
        else lblLogo.setText("LOGO ERP");

        JLabel lblAutores = new JLabel("<html><center>Programación II<br>Universidad ORT Uruguay</center></html>", SwingConstants.CENTER);
        lblAutores.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10)); 
        
        panel.add(lblLogo, BorderLayout.CENTER);
        panel.add(lblAutores, BorderLayout.SOUTH);
        ventana.add(panel);
    }

    // Creamos el método para usar antes de eliminar algo (clave para mistakes!!)
    public static boolean pedirConfirmacion(Component ventanaPadre, String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog(
                ventanaPadre, 
                mensaje, 
                "Confirmar Acción", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        return respuesta == JOptionPane.YES_OPTION;
    }
}