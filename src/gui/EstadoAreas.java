package gui;

import dominio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EstadoAreas extends JDialog { 
    private Sistema sistema;
    private JPanel panelBotones;
    private JList<Area> listaAreas;
    private JLabel labelInfo;

    public EstadoAreas(Sistema sistema, JFrame padre) {
        this.sistema = sistema;
        
        setModal(true);
        setSize(800, 600);
        setTitle("Estado de Áreas");
        setLayout(null);
        
        listaAreas = new JList<>(sistema.getAreasOrdenadasPresupuesto().toArray(new Area[0]));
        
        listaAreas.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Area a = (Area) value;
                double porc = a.getPorcentajeGastado();

                // Colores según letra (en tonos suaves, con letra oscura del mismo color para que se lea)
                if (porc > 90) { setBackground(new Color(253, 226, 225)); setForeground(new Color(138, 28, 28)); }
                else if (porc > 70) { setBackground(new Color(254, 243, 199)); setForeground(new Color(122, 75, 0)); }
                else { setBackground(new Color(241, 245, 249)); setForeground(new Color(31, 41, 51)); }

                if (isSelected) { setBackground(Utilidades.AZUL_MARCA); setForeground(Color.WHITE); }
                setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));     // aire entre filas

                return c;
            }
        });
        
        JScrollPane scroll = new JScrollPane(listaAreas);
        scroll.setBounds(20, 20, 200, 500);
        listaAreas.addListSelectionListener(e -> cargarGrillaEmpleados());
        add(scroll);

        labelInfo = new JLabel("Seleccione un área");
        labelInfo.setBounds(250, 20, 400, 30);
        labelInfo.setFont(labelInfo.getFont().deriveFont(Font.BOLD, 16f));
        add(labelInfo);

        panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(0, 3, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // En el NORTH de un BorderLayout los botones toman su alto normal en vez de estirarse a todo el panel
        JPanel contenedorBotones = new JPanel(new BorderLayout());
        contenedorBotones.add(panelBotones, BorderLayout.NORTH);
        JScrollPane scrollGrid = new JScrollPane(contenedorBotones);
        scrollGrid.setBounds(250, 60, 500, 460);
        add(scrollGrid);

        Utilidades.agregarEncabezado(this, "Estado de áreas", "Rojo: más del 90 % del presupuesto usado  ·  Amarillo: más del 70 %");
        setLocationRelativeTo(padre); // centrar respecto al padre
        Utilidades.establecerIcono(this);
    }

    private void cargarGrillaEmpleados() {
        Area a = listaAreas.getSelectedValue();
        if (a == null) return;
        
        labelInfo.setText(a.getNombre() + " - Presupuesto gastado: " + String.format("%.2f", a.getPorcentajeGastado()) + "%");
        panelBotones.removeAll();

        for(Empleado emp : a.getEmpleados()) {
            JButton btn = new JButton(emp.getNombre());

            // Más salario, azul más intenso (de celeste claro con 0 a azul oscuro desde US$ 5000)
            double intensidad = Math.min(1, emp.getSalarioTotal() / 5000);
            int rojo = (int) (219 - (219 - 20) * intensidad);
            int verde = (int) (234 - (234 - 60) * intensidad);
            int azul = (int) (254 - (254 - 150) * intensidad);
            btn.setBackground(new Color(rojo, verde, azul));
            btn.setForeground(intensidad > 0.6 ? Color.WHITE : new Color(18, 38, 63));     // letra que se lea sobre ese azul
            btn.setContentAreaFilled(false);    // sin el dibujo de Windows, para que se vea el color
            btn.setOpaque(true);
            btn.setFocusPainted(false);
            btn.setFont(btn.getFont().deriveFont(Font.BOLD));
            btn.setPreferredSize(new Dimension(0, 56));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btn.putClientProperty("empleado", emp);
            
            btn.addActionListener(new EscuchaBoton());
            
            panelBotones.add(btn);
        }
        panelBotones.revalidate();
        panelBotones.repaint();
    }


    private class EscuchaBoton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton cual = (JButton) e.getSource();
            
            Empleado emp = (Empleado) cual.getClientProperty("empleado");
            
            if (emp != null) {
                JOptionPane.showMessageDialog(EstadoAreas.this, 
                    "Nombre: " + emp.getNombre() + "\n" +
                    "Cédula: " + emp.getCedula() + "\n" +
                    "Salario total: US$ " + emp.getSalarioTotal() + "\n" +
                    "Manager: " + emp.getManager().getNombre());
            }
        }
    }
}