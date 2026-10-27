package gui;

import dominio.Sistema;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Pantalla de inicio: una tarjeta por tema (Áreas, Managers, Empleados, Reportes) con sus acciones a la vista,
// en lugar de un menú desplegable. Cada tarjeta muestra un resumen que se actualiza al cerrar cada ventana.
public class Menu extends JFrame {
    private Sistema sistema;
    private JLabel lblAreas, lblManagers, lblEmpleados, lblMovimientos;

    public Menu(Sistema sistema) {
        this.sistema = sistema;

        armarGUI();
        actualizarResumen();
        setTitle("ERP NEOCODE TECH - Menú Principal");
        setSize(820, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        Utilidades.establecerIcono(this);

        // Guardar al cerrar
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    sistema.guardarSistema();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error guardando: " + ex.getMessage());
                }
            }
        });
    }

    private void armarGUI() {
        lblAreas = new JLabel();
        lblManagers = new JLabel();
        lblEmpleados = new JLabel();
        lblMovimientos = new JLabel();

        JPanel tarjetas = new JPanel(new GridLayout(2, 2, 16, 16));
        tarjetas.setBackground(new Color(245, 247, 250));
        tarjetas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tarjetas.add(crearTarjeta("Áreas", lblAreas,
                crearBoton("Nueva área", click -> new Areas(sistema, "ALTA", this).setVisible(true)),
                crearBoton("Dar de baja un área", click -> new Areas(sistema, "BAJA", this).setVisible(true)),
                crearBoton("Modificar descripción", click -> new Areas(sistema, "MODIFICACIÓN", this).setVisible(true))));

        tarjetas.add(crearTarjeta("Managers", lblManagers,
                crearBoton("Nuevo manager", click -> new Managers(sistema, "ALTA", this).setVisible(true)),
                crearBoton("Dar de baja un manager", click -> new Managers(sistema, "BAJA", this).setVisible(true)),
                crearBoton("Cambiar celular", click -> new Managers(sistema, "MODIFICACIÓN", this).setVisible(true))));

        tarjetas.add(crearTarjeta("Empleados", lblEmpleados,
                crearBoton("Nuevo empleado", click -> new AltaEmpleado(sistema, this).setVisible(true)),
                crearBoton("Mover a otra área", click -> new Movimientos(sistema, this).setVisible(true))));

        tarjetas.add(crearTarjeta("Reportes", lblMovimientos,
                crearBoton("Estado de áreas", click -> new EstadoAreas(sistema, this).setVisible(true)),
                crearBoton("Movimientos", click -> new ReportesMovimientos(sistema, this).setVisible(true)),
                crearBoton("Reporte inteligente (IA)", click -> new ReporteIA(sistema, this).setVisible(true))));

        JLabel lblAyuda = new JLabel("Tab y Enter para moverse con el teclado  ·  F1: acerca del proyecto", SwingConstants.CENTER);
        lblAyuda.setForeground(new Color(90, 90, 90));
        lblAyuda.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(new Color(245, 247, 250));
        contenido.add(Utilidades.crearEncabezado("ERP NeoCode Tech", "Gestión de personal: elegí qué querés hacer"), BorderLayout.NORTH);
        contenido.add(tarjetas, BorderLayout.CENTER);
        contenido.add(lblAyuda, BorderLayout.SOUTH);
        setContentPane(contenido);

        // F1 (funciona aunque el foco esté en un botón)
        getRootPane().registerKeyboardAction(
                click -> JOptionPane.showMessageDialog(this, "Proyecto académico\nProgramación II - Universidad ORT Uruguay"),
                KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    // Tarjeta blanca con título, resumen y los botones de sus acciones uno debajo del otro.
    private JPanel crearTarjeta(String titulo, JLabel resumen, JButton... botones) {
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 17f));
        lblTitulo.setForeground(new Color(18, 38, 63));
        resumen.setForeground(new Color(90, 90, 90));

        JPanel cabecera = new JPanel(new GridLayout(2, 1));
        cabecera.setOpaque(false);
        cabecera.add(lblTitulo);
        cabecera.add(resumen);

        JPanel acciones = new JPanel(new GridLayout(0, 1, 0, 6));
        acciones.setOpaque(false);
        for (JButton boton : botones) acciones.add(boton);
        // En el NORTH de un BorderLayout los botones toman su alto normal en vez de estirarse a toda la tarjeta
        JPanel contenedorAcciones = new JPanel(new BorderLayout());
        contenedorAcciones.setOpaque(false);
        contenedorAcciones.add(acciones, BorderLayout.NORTH);

        JPanel tarjeta = new JPanel(new BorderLayout(0, 12));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 227, 236)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        tarjeta.add(cabecera, BorderLayout.NORTH);
        tarjeta.add(contenedorAcciones, BorderLayout.CENTER);
        return tarjeta;
    }

    // Botón de una acción. Las ventanas son modales: setVisible espera a que se cierren,
    // así que después se puede actualizar el resumen con lo que haya cambiado.
    private JButton crearBoton(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        Utilidades.estiloBotonSecundario(boton);
        boton.addActionListener(accion);
        boton.addActionListener(click -> actualizarResumen());
        return boton;
    }

    private void actualizarResumen() {
        lblAreas.setText(sistema.getListaAreas().size() + " áreas");
        lblManagers.setText(sistema.getListaManagers().size() + " managers");
        lblEmpleados.setText(sistema.getListaEmpleados().size() + " empleados");
        lblMovimientos.setText(sistema.getListaMovimientos().size() + " movimientos registrados");
    }
}
