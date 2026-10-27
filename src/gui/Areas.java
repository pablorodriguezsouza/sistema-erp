package gui;

import dominio.*;
import java.awt.HeadlessException;
import javax.swing.*;
import java.util.ArrayList;

public class Areas extends JDialog {        // JDialog en lugar de JFrame para poder usar setModal(true), permite bloquear la ventana menú mientras esta ventana está abierta
    private Sistema sistema;
    private JList<Area> listaAreas;
    private JTextField txtNombre, txtPresupuesto, txtDesc;
    private JButton btnAccion; // Un solo botón que cambia de función
    private String modo; // "ALTA", "BAJA", "MODIFICACIÓN"

    public Areas(Sistema sistema, String modo, JFrame padre) {
        this.sistema = sistema;
        this.modo = modo;
        
        setModal(true);
        armarGUI();
        setTitle("Gestión de Áreas - " + modo);
        setSize(520, 400);      // con 500 los campos quedaban pegados al borde derecho
        setLayout(null);
        cargarDatos();
        Utilidades.agregarEncabezado(this, "Gestión de áreas", subtituloSegunModo());
        setLocationRelativeTo(padre); // centrar respecto al padre
        Utilidades.establecerIcono(this);
    }

    private void armarGUI() {
        // Etiqueta dinámica según el filtro
        String tituloLista = modo.equals("BAJA") ? "Áreas eliminables (Vacías):" : "Áreas Registradas:";
        JLabel lblLista = new JLabel(tituloLista);
        lblLista.setBounds(20, 20, 200, 20); add(lblLista);

        listaAreas = new JList<>();
        JScrollPane scroll = new JScrollPane(listaAreas);
        scroll.setBounds(20, 50, 200, 280); 
        add(scroll);
        
        listaAreas.addListSelectionListener(click -> cargarAreaSeleccionada());

        // Campos
        crearLabel("Nombre:", 240, 50);
        txtNombre = crearTxt(330, 50);

        crearLabel("Descripción:", 240, 90);
        txtDesc = crearTxt(330, 90);

        crearLabel("Presupuesto:", 240, 130);
        txtPresupuesto = crearTxt(330, 130);

        // Botón de Acción Principal
        btnAccion = new JButton("Acción");
        btnAccion.setBounds(240, 170, 200, 40);
        btnAccion.addActionListener(click -> ejecutarAccion());
        Utilidades.estiloBotonPrincipal(btnAccion);
        add(btnAccion);

        // Configuración Inicial de la UI según el MODO
        configurarModo();
    }

    private void configurarModo() {
        switch (modo) {
            case "ALTA":
                btnAccion.setText("Crear Área");
                // Todos editables
                txtNombre.setEditable(true);
                txtDesc.setEditable(true);
                txtPresupuesto.setEditable(true);
                break;
                
            case "BAJA":
                btnAccion.setText("Eliminar Seleccionada");
                btnAccion.setEnabled(false); // Se habilita al seleccionar
                // Nada editable, solo lectura
                txtNombre.setEditable(false);
                txtDesc.setEditable(false);
                txtPresupuesto.setEditable(false);
                break;
                
            case "MODIFICACIÓN":
                btnAccion.setText("Guardar Cambios");
                btnAccion.setEnabled(false);
                txtNombre.setEditable(false);
                txtDesc.setEditable(true);
                txtPresupuesto.setEditable(false);
                break;
        }
    }

    private String subtituloSegunModo() {
        switch (modo) {
            case "ALTA": return "Crear un área con su descripción y presupuesto anual";
            case "BAJA": return "Solo se pueden eliminar las áreas sin empleados";
            default: return "Elegí un área para cambiar su descripción";
        }
    }

    private void cargarDatos() {
        ArrayList<Area> listaMostrar = new ArrayList<>();
        
        if (modo.equals("BAJA")) {
            for (Area a : sistema.getAreasOrdenadasNombre()) {
                if (a.getEmpleados().isEmpty()) {
                    listaMostrar.add(a);
                }
            }
        } else {
            listaMostrar = sistema.getAreasOrdenadasNombre();
        }
        
        listaAreas.setListData(listaMostrar.toArray(new Area[0]));
    }

    private void cargarAreaSeleccionada() {
        Area a = listaAreas.getSelectedValue();
        if (a != null) {
            txtNombre.setText(a.getNombre());
            txtDesc.setText(a.getDescripcion());
            txtPresupuesto.setText(String.valueOf(a.getPresupuestoAnual()));
            
            // Habilitar botón si no es ALTA (en Alta el botón siempre está activo para crear)
            if (!modo.equals("ALTA")) {
                btnAccion.setEnabled(true);
            }
        }
    }

    private void ejecutarAccion() {
        switch (modo) {
            case "ALTA":
                altaArea();
                break;
            case "BAJA":
                eliminarArea();
                break;
            case "MODIFICACIÓN":
                modificarArea();
                break;
        }
    }

    private void altaArea() {
        try {
            String nom = txtNombre.getText();
            if (nom.isEmpty()) return;
            
            for(Area a : sistema.getListaAreas()) {
                if (a.getNombre().equalsIgnoreCase(nom)) {
                    JOptionPane.showMessageDialog(this, "Nombre ya existe");
                    return;
                }
            }
            String desc = txtDesc.getText();
            double pre = Double.parseDouble(txtPresupuesto.getText());
            
            sistema.agregarArea(new Area(nom, desc, pre));
            JOptionPane.showMessageDialog(this, "Área creada");
            
            // Limpiar para siguiente alta
            txtNombre.setText(""); txtDesc.setText(""); txtPresupuesto.setText("");
            cargarDatos(); // Refrescar lista
            
        } catch (HeadlessException | NumberFormatException ex) { 
            JOptionPane.showMessageDialog(this, "Datos inválidos"); 
        }
    }

    private void eliminarArea() {
        Area a = listaAreas.getSelectedValue();
        if (a != null) {
            String mensaje = "¿Está seguro de que desea eliminar el área '" + a.getNombre() + "'?";
            boolean confirmado = Utilidades.pedirConfirmacion(this, mensaje); 
            if (confirmado) {
                sistema.eliminarArea(a);
                JOptionPane.showMessageDialog(this, "Área eliminada");
                cargarDatos(); // La lista se actualiza y el área desaparece
                limpiarCampos();
                btnAccion.setEnabled(false);
            }
        }
    }

    private void modificarArea() {
        Area a = listaAreas.getSelectedValue();
        if (a != null) {
            a.setDescripcion(txtDesc.getText());
            JOptionPane.showMessageDialog(this, "Descripción actualizada");
            cargarDatos();
        }
    }
    
    private void limpiarCampos() {
        txtNombre.setText(""); txtDesc.setText(""); txtPresupuesto.setText("");
    }
    
    // Helpers
    private void crearLabel(String t, int x, int y) {
        JLabel l = new JLabel(t); l.setBounds(x, y, 100, 20); add(l);
    }
    private JTextField crearTxt(int x, int y) {
        JTextField t = new JTextField(); t.setBounds(x, y, 150, 20); add(t); return t;
    }
}