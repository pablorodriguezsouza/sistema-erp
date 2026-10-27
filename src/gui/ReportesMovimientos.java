package gui;

import dominio.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ReportesMovimientos extends JDialog {      // JDialog en lugar de JFrame para poder usar setModal(true), permite bloquear la ventana menú mientras esta ventana está abierta
    private Sistema sistema;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> comboFiltroArea, comboFiltroEmpleado;
    private JComboBox<String> comboFiltroMes;

    public ReportesMovimientos(Sistema sistema, JFrame padre) {
        this.sistema = sistema;
        
        setModal(true);
        armarGUI();
        setTitle("Reporte de Movimientos");
        setSize(700, 500);
        setLayout(null);
        cargarFiltros();
        filtrar();
        Utilidades.agregarEncabezado(this, "Reporte de movimientos", "Filtrá por mes, área o empleado y exportá el resultado a CSV");
        setLocationRelativeTo(padre); // centrar respecto al padre
        Utilidades.establecerIcono(this);
    }

    private void armarGUI() {
        JLabel labelMes = new JLabel("Mes:");
        labelMes.setBounds(20, 10, 30, 20); 
        add(labelMes);
        comboFiltroMes = new JComboBox<>();
        comboFiltroMes.addItem("Todos");
        for(int i=1; i<=12; i++) comboFiltroMes.addItem(String.valueOf(i));
        comboFiltroMes.setBounds(50, 10, 70, 20);
        comboFiltroMes.addActionListener(click -> filtrar());
        add(comboFiltroMes);

        JLabel labelArea = new JLabel("Área:");
        labelArea.setBounds(130, 10, 40, 20); 
        add(labelArea);
        comboFiltroArea = new JComboBox<>();
        comboFiltroArea.setBounds(170, 10, 150, 20);
        comboFiltroArea.addActionListener(click -> filtrar());
        add(comboFiltroArea);


        JLabel labelEmpleado = new JLabel("Emp:");
        labelEmpleado.setBounds(330, 10, 40, 20); 
        add(labelEmpleado);
        comboFiltroEmpleado = new JComboBox<>();
        comboFiltroEmpleado.setBounds(370, 10, 150, 20);
        comboFiltroEmpleado.addActionListener(click -> filtrar());
        add(comboFiltroEmpleado);
        
        JButton btnExportar = new JButton("Exportar CSV");
        btnExportar.setBounds(540, 10, 130, 20);
        btnExportar.addActionListener(click -> exportarCSV());
        add(btnExportar);

        String[] columnas = {"Mes", "Origen", "Destino", "Empleado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Esto hace que ninguna celda se pueda editar
            }
        };
        
        tabla = new JTable(modeloTabla);
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 50, 650, 400);
        add(scroll);
    }

    private void cargarFiltros() {
        comboFiltroArea.addItem("Todas");
        for(Area a : sistema.getListaAreas()) {
            comboFiltroArea.addItem(a.getNombre());
        }

        comboFiltroEmpleado.addItem("Todos");
        for(Empleado e : sistema.getListaEmpleados()) {
            comboFiltroEmpleado.addItem(e.getNombre());
        }
    }

    private void filtrar() {
        if(comboFiltroMes.getSelectedItem() == null || comboFiltroArea.getSelectedItem() == null || comboFiltroEmpleado.getSelectedItem() == null) { 
            return; 
        }
        modeloTabla.setRowCount(0);

        String fMes = (String) comboFiltroMes.getSelectedItem();
        String fArea = (String) comboFiltroArea.getSelectedItem();
        String fEmp = (String) comboFiltroEmpleado.getSelectedItem();

        ArrayList<Movimiento> lista = sistema.getListaMovimientos();
        java.util.Collections.sort(lista, new java.util.Comparator<Movimiento>() {
            public int compare(Movimiento m1, Movimiento m2) {
                return m1.getMes() - m2.getMes();
            }
        });

        for (Movimiento m : lista) {
            boolean cumpleMes = fMes.equals("Todos") || String.valueOf(m.getMes()).equals(fMes);
            boolean cumpleEmp = fEmp.equals("Todos") || m.getNombreEmpleado().equals(fEmp);
            boolean cumpleArea = fArea.equals("Todas") || m.getNombreOrigen().equals(fArea) || m.getNombreDestino().equals(fArea);

            if (cumpleMes && cumpleEmp && cumpleArea) {
                Object[] fila = {m.getMes(), m.getNombreOrigen(), m.getNombreDestino(), m.getNombreEmpleado()};
                modeloTabla.addRow(fila);
            }
        }
    }  

    private void exportarCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getAbsolutePath();
                if (!path.endsWith(".csv")) path += ".csv";
                
                PrintWriter pw = new PrintWriter(new FileWriter(path));
                pw.println("Mes;Area Origen;Area Destino;Nombre Empleado");
                
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    String linea = modeloTabla.getValueAt(i, 0) + ";" +
                                   modeloTabla.getValueAt(i, 1) + ";" +
                                   modeloTabla.getValueAt(i, 2) + ";" +
                                   modeloTabla.getValueAt(i, 3);
                    pw.println(linea);
                }
                pw.close();
                JOptionPane.showMessageDialog(this, "Exportado correctamente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exportando: " + ex.getMessage());
            }
        }
    }
}