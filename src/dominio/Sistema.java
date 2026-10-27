package dominio;

import java.io.*;
import java.util.*;

public class Sistema implements Serializable {
    private ArrayList<Area> listaAreas;
    private ArrayList<Manager> listaManagers;
    private ArrayList<Empleado> listaEmpleados;
    private ArrayList<Movimiento> listaMovimientos;

    public Sistema() {
        listaAreas = new ArrayList<>();
        listaManagers = new ArrayList<>();
        listaEmpleados = new ArrayList<>();
        listaMovimientos = new ArrayList<>();
    }

    public ArrayList<Area> getListaAreas() { return listaAreas; }
    public ArrayList<Manager> getListaManagers() { return listaManagers; }
    public ArrayList<Empleado> getListaEmpleados() { return listaEmpleados; }
    public ArrayList<Movimiento> getListaMovimientos() { return listaMovimientos; }

    public void agregarArea(Area area) { listaAreas.add(area); }
    public void agregarManager(Manager manager) { listaManagers.add(manager); }

    public void eliminarArea(Area area) { listaAreas.remove(area); }
    public void eliminarManager(Manager manager) { listaManagers.remove(manager); }
    
    public void agregarEmpleado(Empleado empleado) { 
        listaEmpleados.add(empleado);
        empleado.getArea().agregarEmpleado(empleado);
        empleado.getManager().agregarSubordinado(empleado);
        empleado.getArea().descontarPresupuesto(empleado.getSalarioTotal() * 12);
    }

    // Lógica de Movimiento
    public boolean realizarMovimiento(Empleado empleado, Area destino, int mesInicio) {
        Area origen = empleado.getArea();
        double costoRestante = empleado.getSalarioTotal() * (13 - mesInicio);

        if (destino.getPresupuestoDisponible() >= costoRestante) {
            origen.removerEmpleado(empleado);
            origen.reintegrarPresupuesto(costoRestante);
            destino.agregarEmpleado(empleado);
            destino.descontarPresupuesto(costoRestante);
            empleado.setArea(destino);
            
            // Registro para reporte
            Movimiento mov = new Movimiento(mesInicio, origen.getNombre(), destino.getNombre(), empleado.getNombre());
            listaMovimientos.add(mov);
            
            return true;
        }
        return false;
    }

    public void guardarSistema() throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("sistema.ser"));
        out.writeObject(this);
        out.close();
    }

    public static Sistema cargarSistema() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("sistema.ser"));
        Sistema sistema = (Sistema) in.readObject();
        in.close();
        return sistema;
    }

    public void cargarDatosFicticios() {
        Area a1 = new Area("Personal", "Reclutamiento de personal, promociones, gestión de cargos", 100000);
        Area a2 = new Area("RRHH", "Relacionamiento en la empresa, organigrama, gestión de equipos", 80000);
        Area a3 = new Area("Seguridad", "Seguridad física, vigilancia, seguridad informática, protocolos y políticas de seguridad", 120000);
        Area a4 = new Area("Comunicaciones", "Comunicaciones internas, reglas y protocolos, comunicaciones con proveedores y clientes", 20000);
        Area a5 = new Area("Marketing", "Acciones planificadas, publicidad en medios masivos, publicidad en redes, gestión de redes", 95000);
        
        Manager m1 = new Manager("Ana Martínez", "4.568.369-1", "099 123456", 10);
        Manager m2 = new Manager("Ricardo Morales", "3.214.589-3", "094 121212", 4);
        Manager m3 = new Manager("Laura Torales", "3.589.257-5", "099 654321", 1);
        Manager m4 = new Manager("Juan Pablo Zapata", "4.555.197-7", "099 202020", 5);
        
        agregarArea(a1); agregarArea(a2); 
        agregarArea(a3); agregarArea(a4); 
        agregarArea(a5);

        agregarManager(m1); agregarManager(m2); 
        agregarManager(m3); agregarManager(m4);
    }
    
    public ArrayList<Area> getAreasOrdenadasPresupuesto() {
        ArrayList<Area> listaOrdenada = new ArrayList<>(listaAreas);
        Collections.sort(listaOrdenada, Area.ORDEN_POR_PRESUPUESTO);
        return listaOrdenada;
    }
    
    public ArrayList<Area> getAreasOrdenadasNombre() {
        ArrayList<Area> listaOrdenada = new ArrayList<>(listaAreas);
        Collections.sort(listaOrdenada);
        return listaOrdenada;
    }
    
    public ArrayList<Empleado> getEmpleadosOrdenadosSalario() {
        ArrayList<Empleado> listaOrdenada = new ArrayList<>(listaEmpleados);
        Collections.sort(listaOrdenada);
        return listaOrdenada;
    }
    
    public ArrayList<Manager> getManagersOrdenadosAntiguedad() {
        ArrayList<Manager> listaOrdenada = new ArrayList<>(listaManagers);
        Collections.sort(listaOrdenada);
        return listaOrdenada;
    }

    public boolean existeCedula(String ci) {
        boolean existe = false;
        for (Manager m : listaManagers) {
            if (m.getCedula().equals(ci)) existe = true;
        }
        for (Empleado e : listaEmpleados) {
            if (e.getCedula().equals(ci)) existe = true;
        }
        return existe;
    }
}

