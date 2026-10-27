package dominio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Area implements Serializable, Comparable<Area>{
    private String nombre;
    private String descripcion;
    private double presupuestoAnual;
    private double presupuestoGastado;
    private ArrayList<Empleado> empleados;

    public Area(String nombre, String descripcion, double presupuestoAnual) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.presupuestoAnual = presupuestoAnual;
        this.presupuestoGastado = 0;
        this.empleados = new ArrayList<>();
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPresupuestoAnual() { return presupuestoAnual; }
    public double getPresupuestoDisponible() { return presupuestoAnual - presupuestoGastado; }
    public ArrayList<Empleado> getEmpleados() { return empleados; }
    
    public void setDescripcion(String d) { this.descripcion = d; }
    
    public void agregarEmpleado(Empleado e) { empleados.add(e); }
    public void removerEmpleado(Empleado e) { empleados.remove(e); }
    

    // para reporte
    public double getPorcentajeGastado() {
        if (presupuestoAnual == 0) return 0;
        return (presupuestoGastado / presupuestoAnual) * 100;
    }

    public void descontarPresupuesto(double monto) {
        this.presupuestoGastado += monto;
    }

    public void reintegrarPresupuesto(double monto) {
        this.presupuestoGastado -= monto;
    }

    public static final Comparator<Area> ORDEN_POR_PRESUPUESTO = new Comparator<Area>() {
        @Override
        public int compare(Area a1, Area a2) {
            return Double.compare(a2.getPorcentajeGastado(), a1.getPorcentajeGastado());
        }
    };
    
    @Override
    public int compareTo(Area otraArea) {
        return this.getNombre().compareToIgnoreCase(otraArea.getNombre());
    }
    
    @Override
    public String toString() {
        return nombre + " (Disp: US$" + getPresupuestoDisponible() + ")";
    }
}