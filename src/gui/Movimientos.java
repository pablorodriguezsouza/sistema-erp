package gui;
import dominio.*;
import javax.swing.*;
import java.awt.*;

public class Movimientos extends JDialog {     // JDialog en lugar de JFrame para poder usar setModal(true), permite bloquear la ventana menú mientras esta ventana está abierta
    private Sistema sistema;
    private JComboBox<Area> cmbOrigen, cmbDestino;
    private JComboBox<Empleado> cmbEmpleadoMov;
    private JComboBox<Integer> cmbMes;

    public Movimientos(Sistema sistema, JFrame padre) {
        this.sistema = sistema;
        
        setModal(true);
        armarGUI();
        setTitle("Realizar Movimiento");
        setSize(500, 350);
        setLayout(null);
        cargarDatos();
        Utilidades.agregarEncabezado(this, "Realizar movimiento", "Transferencia de personal: el costo de los meses que faltan pasa al área de destino");
        setLocationRelativeTo(padre); // centrar respecto al padre, es mas responsive que setBounds
        Utilidades.establecerIcono(this);
    }

    private void armarGUI() {
        // Mes
        crearLabel("Mes del Movimiento:", 50, 60);
        cmbMes = new JComboBox<>();
        for(int i=1; i<=12; i++) cmbMes.addItem(i);
        cmbMes.setBounds(200, 60, 100, 25); add(cmbMes);
        
        // Origen
        crearLabel("Área Origen:", 50, 100);
        cmbOrigen = new JComboBox<>();
        cmbOrigen.setBounds(200, 100, 200, 25);
        cmbOrigen.addActionListener(click -> actualizarEmpleadosOrigen());
        add(cmbOrigen);
        
        // Empleado
        crearLabel("Empleado:", 50, 140);
        cmbEmpleadoMov = new JComboBox<>();
        cmbEmpleadoMov.setBounds(200, 140, 200, 25); add(cmbEmpleadoMov);
        
        // Destino
        crearLabel("Área Destino:", 50, 180);
        cmbDestino = new JComboBox<>();
        cmbDestino.setBounds(200, 180, 200, 25); add(cmbDestino);
        
        // Botón
        JButton btnMover = new JButton("Confirmar Movimiento");
        btnMover.setBounds(150, 240, 200, 40);
        btnMover.addActionListener(click -> realizarMovimiento());
        Utilidades.estiloBotonPrincipal(btnMover);
        add(btnMover);
    }

    private void cargarDatos() {
        cmbOrigen.removeAllItems();
        cmbDestino.removeAllItems();
        for(Area a : sistema.getListaAreas()) {
            cmbOrigen.addItem(a);
            cmbDestino.addItem(a);
        }
    }

    private void actualizarEmpleadosOrigen() {
        cmbEmpleadoMov.removeAllItems();
        Area origen = (Area) cmbOrigen.getSelectedItem();
        if (origen != null) {
            for(Empleado e : origen.getEmpleados()) cmbEmpleadoMov.addItem(e);
        }
    }

    private void realizarMovimiento() {
        Empleado emp = (Empleado) cmbEmpleadoMov.getSelectedItem();
        Area dest = (Area) cmbDestino.getSelectedItem();
        
        if (emp == null || dest == null) return;
        
        int mes = (Integer) cmbMes.getSelectedItem();
        
        if (sistema.realizarMovimiento(emp, dest, mes)) {
            JOptionPane.showMessageDialog(this, "Movimiento Exitoso");
            actualizarEmpleadosOrigen();
        } else {
            JOptionPane.showMessageDialog(this, "Error: Presupuesto insuficiente en destino.");
        }
    }
    
    private void crearLabel(String t, int x, int y) {
        JLabel l = new JLabel(t); l.setBounds(x, y, 150, 20); add(l);
    }
}