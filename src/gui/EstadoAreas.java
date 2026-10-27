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
                
                // Colores según letra
                if (porc > 90) setBackground(Color.RED);
                else if (porc > 70) setBackground(Color.YELLOW);
                else setBackground(Color.LIGHT_GRAY);
                
                if (isSelected) setBackground(Color.BLUE); 
                
                return c;
            }
        });
        
        JScrollPane scroll = new JScrollPane(listaAreas);
        scroll.setBounds(20, 20, 200, 500);
        listaAreas.addListSelectionListener(e -> cargarGrillaEmpleados());
        add(scroll);

        labelInfo = new JLabel("Seleccione un área");
        labelInfo.setBounds(250, 20, 400, 30);
        labelInfo.setFont(new Font("Arial", Font.BOLD, 16));
        add(labelInfo);

        panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(0, 3, 10, 10)); 
        JScrollPane scrollGrid = new JScrollPane(panelBotones);
        scrollGrid.setBounds(250, 60, 500, 460);
        add(scrollGrid);

        Utilidades.agregarEncabezado(this, "Estado de áreas", "Rojo: más del 90 % del presupuesto usado  ·  Amarillo: más del 70 %");
        setLocationRelativeTo(padre); // centrar respecto al padre
        Utilidades.establecerIcono(this);
    }

    private void cargarGrillaEmpleados() {
        Area a = listaAreas.getSelectedValue();
        if (a == null) return;
        
        labelInfo.setText(a.getNombre() + " - Presupuesto en gastado: " + String.format("%.2f", a.getPorcentajeGastado()) + "%");
        panelBotones.removeAll();
        
        for(Empleado emp : a.getEmpleados()) {
            JButton btn = new JButton(emp.getNombre());
            
            int azul = (int) Math.min(255, (emp.getSalarioTotal() / 5000) * 255); 
            btn.setBackground(new Color(0, 0, azul));
            btn.setForeground(Color.WHITE);
            
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