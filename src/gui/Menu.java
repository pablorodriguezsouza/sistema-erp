package gui;

import dominio.Sistema;
import java.awt.event.*;
import javax.swing.*;

public class Menu extends JFrame {
    private Sistema sistema;

    public Menu(Sistema sistema) {
        this.sistema = sistema;
        
        armarGUI();
        setTitle("ERP NEOCODE TECH - Menú Principal");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        
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
        Utilidades.cargarLogoPrincipal(this);
    }

    private void armarGUI() {
            JMenuBar menuBar = new JMenuBar();

            // MENÚ ÁREAS
            JMenu menuAreas = new JMenu("Áreas");

            JMenuItem itemAltaArea = new JMenuItem("Alta");
            itemAltaArea.addActionListener(click -> new Areas(sistema, "ALTA", this).setVisible(true));
            menuAreas.add(itemAltaArea);

            JMenuItem itemBajaArea = new JMenuItem("Baja");
            itemBajaArea.addActionListener(click -> new Areas(sistema, "BAJA", this).setVisible(true));
            menuAreas.add(itemBajaArea);

            JMenuItem itemModifArea = new JMenuItem("Modificación");
            itemModifArea.addActionListener(click -> new Areas(sistema, "MODIFICACIÓN", this).setVisible(true));
            menuAreas.add(itemModifArea);

            JMenuItem itemMovimiento = new JMenuItem("Realizar Movimiento");
            itemMovimiento.addActionListener(click -> new Movimientos(sistema, this).setVisible(true));
            menuAreas.add(itemMovimiento);

            menuBar.add(menuAreas);

            // MANAGERS
            JMenu menuManagers = new JMenu("Managers");

            JMenuItem itemAltaMgr = new JMenuItem("Alta");
            itemAltaMgr.addActionListener(click -> new Managers(sistema, "ALTA", this).setVisible(true));
            menuManagers.add(itemAltaMgr);

            JMenuItem itemBajaMgr = new JMenuItem("Baja");
            itemBajaMgr.addActionListener(click -> new Managers(sistema, "BAJA", this).setVisible(true));
            menuManagers.add(itemBajaMgr);

            JMenuItem itemModifMgr = new JMenuItem("Modificación");
            itemModifMgr.addActionListener(click -> new Managers(sistema, "MODIFICACIÓN", this).setVisible(true));
            menuManagers.add(itemModifMgr);

            menuBar.add(menuManagers);

            // MENÚ EMPLEADOS
            JMenu menuEmps = new JMenu("Empleados");
            JMenuItem itemEmpsAlta = new JMenuItem("Alta");
            itemEmpsAlta.addActionListener(click -> new AltaEmpleado(sistema, this).setVisible(true));
            menuEmps.add(itemEmpsAlta);
            menuBar.add(menuEmps);

            // MENÚ REPORTES
            JMenu menuReportes = new JMenu("Reportes");

            JMenuItem itemRepIA = new JMenuItem("Reporte Inteligente (IA)");
            itemRepIA.addActionListener(click -> new ReporteIA(sistema, this).setVisible(true));
            menuReportes.add(itemRepIA);

            JMenuItem itemRepEstado = new JMenuItem("Estado de Áreas");
            itemRepEstado.addActionListener(click -> new EstadoAreas(sistema, this).setVisible(true));
            menuReportes.add(itemRepEstado);

            JMenuItem itemRepMov = new JMenuItem("Reporte Movimientos");
            itemRepMov.addActionListener(click -> new ReportesMovimientos(sistema, this).setVisible(true));
            menuReportes.add(itemRepMov);

            menuBar.add(menuReportes);

            setJMenuBar(menuBar);

            // F1
            this.setFocusable(true);
            this.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent key) {
                    if (key.getKeyCode() == KeyEvent.VK_F1) {
                        JOptionPane.showMessageDialog(null, "Proyecto académico\nProgramación II - Universidad ORT Uruguay");
                    }
                }
            });
        }
}
