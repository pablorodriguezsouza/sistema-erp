package gui;

import dominio.*;
import java.io.IOException;
import java.util.Formatter;
import javax.swing.*;

public class AltaEmpleado extends JDialog  {        // JDialog en lugar de JFrame para poder usar setModal(true), permite bloquear la ventana menú mientras esta ventana está abierta
    private Sistema sistema;
    private JTextField txtNombre, txtCedula, txtCelular, txtSalario, txtSalarioExtra;
    private JTextArea txtCV;
    private JComboBox<Manager> comboManager;
    private JComboBox<Area> comboArea;
    private JList<Empleado> listaEmpleados;

    public AltaEmpleado(Sistema sistema, JFrame padre) {
        this.sistema = sistema;
        
        setModal(true);
        armarGUI();
        setSize(600, 400);
        setLayout(null);
        setTitle("Alta de Empleados");
        cargarListas();
        setLocationRelativeTo(padre); // centrar respecto al padre, es mas responsive que setBounds
        Utilidades.establecerIcono(this);
    }

    private void armarGUI() {
        crearLabel("Nombre:", 20, 20); 
        txtNombre = crearTxt(100, 20);
        crearLabel("Cédula:", 20, 50); 
        txtCedula = crearTxt(100, 50);
        crearLabel("Celular:", 20, 80); 
        txtCelular = crearTxt(100, 80);
        crearLabel("Salario:", 20, 110); 
        txtSalario = crearTxt(100, 110);
        crearLabel("Salario Extra:", 20, 140); 
        txtSalarioExtra = crearTxt(100, 140);
        
        crearLabel("CV:", 20, 170);
        txtCV = new JTextArea();
        JScrollPane scrollCV = new JScrollPane(txtCV);
        scrollCV.setBounds(100, 170, 200, 80);
        add(scrollCV);

        crearLabel("Manager:", 20, 260);
        comboManager = new JComboBox<>();
        comboManager.setBounds(100, 260, 200, 25);
        add(comboManager);

        crearLabel("Área:", 20, 290);
        comboArea = new JComboBox<>();
        comboArea.setBounds(100, 290, 200, 25);
        add(comboArea);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(100, 330, 100, 30);
        btnAgregar.addActionListener(click -> agregarEmpleado());
        add(btnAgregar);

        listaEmpleados = new JList<>();
        JScrollPane scrollLista = new JScrollPane(listaEmpleados);
        scrollLista.setBounds(320, 20, 250, 300);
        add(scrollLista);
        
        listaEmpleados.addListSelectionListener(click -> {
            if (!click.getValueIsAdjusting()) mostrarDetalle(listaEmpleados.getSelectedValue());
        });
    }

    private void agregarEmpleado() {
            try {
                String nombre = txtNombre.getText();
                String ci = txtCedula.getText();
                String cel = txtCelular.getText();
                String cv = txtCV.getText();
                double salario = Double.parseDouble(txtSalario.getText());
                double salarioExtra = txtSalarioExtra.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtSalarioExtra.getText());
                Manager manager = (Manager) comboManager.getSelectedItem();
                Area area = (Area) comboArea.getSelectedItem();

                if (manager == null || area == null) {
                    JOptionPane.showMessageDialog(this, "Seleccione Area y Manager");

                } else {
                    if (area.getPresupuestoDisponible() < (salario +  salarioExtra) * 12) {
                        JOptionPane.showMessageDialog(this, "<html> No hay presupuesto en el área seleccionada.<br>" +
                        "Costo Anual Requerido (12 salarios): <font color='red'>US$ " + ((salario +  salarioExtra) * 12) + "</font><br>" +
                        "Presupuesto Disponible: US$ " + area.getPresupuestoDisponible() + "<br>" +
                        "El salario maximo mensual disponible es: <font color='blue'>US$ " + String.format("%.2f",(area.getPresupuestoDisponible()/12)) + "</html>");
                    } else {
                        if (sistema.existeCedula(ci)) { 
                            JOptionPane.showMessageDialog(this, "Esa cédula ya está registrada en el sistema.");
                        } else {                        
                            Empleado nuevo = new Empleado(nombre, ci, cel, cv, salario, manager, area, salarioExtra);
                            sistema.agregarEmpleado(nuevo);

                            guardarCVEnArchivo(ci, cv);
                            cargarListas();
                            limpiar();
                            JOptionPane.showMessageDialog(this, "Empleado agregado");
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error en salario numérico");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
    }

    
    private void guardarCVEnArchivo(String cedula, String texto) {
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("cvs")); // Crear carpeta si no existe
            Formatter arch = new Formatter("cvs/CV" + cedula + ".txt");
            arch.format("%s", texto);
            arch.close();
        } catch (IOException ex) {
            System.out.println("Error guardando CV");
        }
    }

    private void cargarListas() {
        comboManager.removeAllItems();
        for (Manager m : sistema.getListaManagers()) {
            comboManager.addItem(m);
        }
        
        comboArea.removeAllItems();
        for (Area a : sistema.getListaAreas()) {
            comboArea.addItem(a);
        }
        
        listaEmpleados.setListData(sistema.getEmpleadosOrdenadosSalario().toArray(new Empleado[0]));
    }
    
    private void mostrarDetalle(Empleado empleado) {
        if (empleado != null) {
            JOptionPane.showMessageDialog(this, empleado.toString() + "\nÁrea: " + empleado.getArea().getNombre());
        }
    }

    private void limpiar() {
        txtNombre.setText(""); txtCedula.setText(""); txtCV.setText(""); txtCelular.setText("");
    }
    
    private void crearLabel(String txt, int x, int y) {
        JLabel label = new JLabel(txt); label.setBounds(x, y, 80, 20); 
        add(label);
    }
    
    private JTextField crearTxt(int x, int y) {
        JTextField txt = new JTextField(); txt.setBounds(x, y, 200, 20); 
        add(txt); 
        return txt;
    }
}
