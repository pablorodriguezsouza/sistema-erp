package gui;

import javax.swing.*;
import java.awt.*;


public class Utilidades {

    // Colores de la marca NeoCode (tomados del logo), oscurecidos para que el texto blanco se lea bien encima
    public static final Color AZUL_MARCA = new Color(27, 79, 194);
    public static final Color AZUL_MARCA_OSCURO = new Color(20, 60, 150);
    public static final Color VERDE_MARCA = new Color(11, 127, 110);

    // Franja de color con título y subtítulo (la usan el menú principal y agregarEncabezado).
    public static JPanel crearEncabezado(String titulo, String subtitulo) {
        JPanel encabezado = new JPanel(new GridLayout(2, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, AZUL_MARCA, getWidth(), 0, VERDE_MARCA));   // de azul a verde, como el logo
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        encabezado.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        encabezado.setPreferredSize(new Dimension(0, 64));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 18f));
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setForeground(Color.WHITE);
        encabezado.add(lblTitulo);
        encabezado.add(lblSubtitulo);
        return encabezado;
    }

    // Pone la franja arriba de una ventana. Envuelve el panel que ya tiene la ventana (con sus setBounds)
    // y agranda la ventana lo que ocupa la franja.
    public static void agregarEncabezado(JDialog ventana, String titulo, String subtitulo) {
        JPanel encabezado = crearEncabezado(titulo, subtitulo);
        Container contenidoAnterior = ventana.getContentPane();
        JPanel nuevoContenido = new JPanel(new BorderLayout());
        nuevoContenido.add(encabezado, BorderLayout.NORTH);
        nuevoContenido.add(contenidoAnterior, BorderLayout.CENTER);
        ventana.setContentPane(nuevoContenido);
        ventana.setSize(ventana.getWidth(), ventana.getHeight() + 64);
    }

    // Botón de la acción principal de cada ventana: azul de la marca, letra blanca, más oscuro con el mouse
    // encima y gris mientras está deshabilitado.
    public static void estiloBotonPrincipal(JButton boton) {
        boton.setBackground(AZUL_MARCA);
        boton.setForeground(Color.WHITE);
        boton.setFont(boton.getFont().deriveFont(Font.BOLD));
        boton.setContentAreaFilled(false);      // sin el dibujo de Windows, para que se vea el color
        boton.setOpaque(true);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        marcarFoco(boton);
        boton.getModel().addChangeListener(cambio -> {
            if (!boton.isEnabled()) boton.setBackground(new Color(214, 218, 224));
            else if (boton.getModel().isRollover()) boton.setBackground(AZUL_MARCA_OSCURO);
            else boton.setBackground(AZUL_MARCA);
        });
    }

    // Botón de las acciones del menú principal: celeste claro con letra azul, más oscuro con el mouse encima.
    public static void estiloBotonSecundario(JButton boton) {
        Color fondo = new Color(236, 242, 252);
        boton.setBackground(fondo);
        boton.setForeground(AZUL_MARCA_OSCURO);
        boton.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 13f));     // la misma letra que el resto de la app
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setContentAreaFilled(false);      // sin el dibujo de Windows, para que se vea el color
        boton.setOpaque(true);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        marcarFoco(boton);
        boton.getModel().addChangeListener(cambio -> boton.setBackground(boton.getModel().isRollover() ? new Color(214, 228, 250) : fondo));
    }

    // Borde que muestra qué botón tiene el foco del teclado (al recorrer con Tab), sin cambiar su tamaño.
    // Además el botón se activa con Enter, no solo con Espacio (lo que trae Swing).
    private static void marcarFoco(JButton boton) {
        boton.getInputMap().put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0, false), "pressed");
        boton.getInputMap().put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0, true), "released");
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        boton.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(18, 38, 63), 2),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                boton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            }
        });
    }

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