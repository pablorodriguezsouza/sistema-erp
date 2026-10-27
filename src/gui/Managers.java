package gui;

import dominio.*;
import java.awt.HeadlessException;
import javax.swing.*;
import java.util.ArrayList;

public class Managers extends JDialog {     // JDialog y no JFrame para poder usar setModal(true), esto permite bloquear la ventana menú
    private Sistema sistema;
    private JList<Manager> lstManagers;
    private JTextField txtNombre, txtCedula, txtAntiguedad, txtCelular;
    private JButton btnAccion;
    private String modo; // ALTA, BAJA, MODIFICACIÓN

    public Managers(Sistema sistema, String modo, JFrame padre) {
        this.sistema = sistema;
        this.modo = modo;
        
        setModal(true);
        armarGUI();
        setTitle("Gestión de Managers - " + modo);
        setSize(500, 400);
        setLayout(null);
        cargarLista();
        Utilidades.agregarEncabezado(this, "Gestión de managers", subtituloSegunModo());
        setLocationRelativeTo(padre); // centrar respecto al padre, es mas responsive que setBounds
        Utilidades.establecerIcono(this);
    }

    private void armarGUI() {
        String titulo = modo.equals("BAJA") ? "Sin subordinados:" : "Managers:";
        JLabel lblLista = new JLabel(titulo);
        lblLista.setBounds(20, 20, 150, 20); add(lblLista);

        lstManagers = new JList<>();
        JScrollPane scroll = new JScrollPane(lstManagers);
        scroll.setBounds(20, 50, 200, 280); add(scroll);
        
        lstManagers.addListSelectionListener(click -> cargarManagerSeleccionado());

        // Campos
        int xL = 240, xT = 320;
        addLabel("Nombre:", xL, 50); txtNombre = addTxt(xT, 50);
        addLabel("Cédula:", xL, 90); txtCedula = addTxt(xT, 90);
        addLabel("Antigüed:", xL, 130); txtAntiguedad = addTxt(xT, 130);
        addLabel("Celular:", xL, 170); txtCelular = addTxt(xT, 170);

        btnAccion = new JButton("Acción");
        btnAccion.setBounds(240, 220, 200, 30);
        btnAccion.addActionListener(click -> ejecutarAccion());
        Utilidades.estiloBotonPrincipal(btnAccion);
        add(btnAccion);

        configurarModo();
    }

    private void configurarModo() {
        switch (modo) {
            case "ALTA":
                btnAccion.setText("Crear Manager");
                hacerEditables(true, true, true, true);
                break;
            case "BAJA":
                btnAccion.setText("Eliminar");
                btnAccion.setEnabled(false);
                hacerEditables(false, false, false, false);
                break;
            case "MODIFICACIÓN":
                btnAccion.setText("Guardar Celular");
                btnAccion.setEnabled(false);
                txtNombre.setEditable(false);
                txtCedula.setEditable(false);
                txtAntiguedad.setEditable(false);
                txtCelular.setEditable(true);
                break;
        }
    }

    private String subtituloSegunModo() {
        switch (modo) {
            case "ALTA": return "Registrar un manager con su antigüedad en la empresa";
            case "BAJA": return "Solo se pueden eliminar los managers sin empleados a cargo";
            default: return "Elegí un manager para cambiar su celular";
        }
    }

    private void cargarLista() {
        ArrayList<Manager> mostrar = new ArrayList<>();
        if (modo.equals("BAJA")) {
            for (Manager m : sistema.getManagersOrdenadosAntiguedad()) {
                if (m.getSubordinados().isEmpty()) mostrar.add(m);
            }
        } else {
            mostrar = sistema.getManagersOrdenadosAntiguedad();
        }
        lstManagers.setListData(mostrar.toArray(new Manager[0]));
    }

    private void cargarManagerSeleccionado() {
        Manager m = lstManagers.getSelectedValue();
        if (m != null) {
            txtNombre.setText(m.getNombre());
            txtCedula.setText(m.getCedula());
            txtAntiguedad.setText(String.valueOf(m.getAntiguedad()));
            txtCelular.setText(m.getCelular());
            if (!modo.equals("ALTA")) btnAccion.setEnabled(true);
        }
    }

    private void ejecutarAccion() {
        switch (modo) {
            case "ALTA":
                try {
                    if(sistema.existeCedula(txtCedula.getText())) {
                        JOptionPane.showMessageDialog(this, "Cédula repetida"); return;
                    }
                    Manager n = new Manager(txtNombre.getText(), txtCedula.getText(), txtCelular.getText(), Integer.parseInt(txtAntiguedad.getText()));
                    sistema.agregarManager(n);
                    JOptionPane.showMessageDialog(this, "Creado");
                    limpiar(); cargarLista();
                } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error datos"); }
                break;
            case "BAJA":
                {
                    Manager m = lstManagers.getSelectedValue();
                    if (m != null) {
                        String mensaje = "¿Está seguro de que desea eliminar el manager '" + m.getNombre() + "'?";
                        boolean confirmado = Utilidades.pedirConfirmacion(this, mensaje);
                        
                        if (confirmado) {
                            sistema.eliminarManager(m);
                            JOptionPane.showMessageDialog(this, "Eliminado");
                            limpiar(); cargarLista();
                        }
                    }
                    break;
                }
            case "MODIFICACIÓN":
                {
                    Manager m = lstManagers.getSelectedValue();
                    if (m != null) {
                        m.setCelular(txtCelular.getText());
                        JOptionPane.showMessageDialog(this, "Modificado");
                        cargarLista();
                    }
                    break;
                }
            default:
            break;
        }
    }

    private void limpiar() {
        txtNombre.setText(""); txtCedula.setText(""); txtAntiguedad.setText(""); txtCelular.setText("");
    }
    
    private void hacerEditables(boolean n, boolean c, boolean a, boolean cel) {
        txtNombre.setEditable(n); txtCedula.setEditable(c); txtAntiguedad.setEditable(a); txtCelular.setEditable(cel);
    }
    
    private void addLabel(String t, int x, int y) { JLabel l = new JLabel(t); l.setBounds(x, y, 70, 20); add(l); }
    private JTextField addTxt(int x, int y) { JTextField t = new JTextField(); t.setBounds(x, y, 130, 20); add(t); return t; }
}