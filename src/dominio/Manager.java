package dominio;
import java.io.Serializable;
import java.util.ArrayList;

public class Manager implements Serializable, Comparable<Manager>{
    private String nombre;
    private String cedula;
    private String celular;
    private int antiguedad;
    private ArrayList<Empleado> subordinados;

    public Manager(String nombre, String cedula, String celular, int antiguedad) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.celular = celular;
        this.antiguedad = antiguedad;
        this.subordinados = new ArrayList<>();
    }
    
    public String getNombre() { return nombre; }
    public String getCedula() { return cedula; }
    public String getCelular() { return celular; }
    public int getAntiguedad() { return antiguedad; }
    public ArrayList<Empleado> getSubordinados() { return subordinados; }

    public void setCelular(String c) { this.celular = c; }
    
    public void agregarSubordinado(Empleado empleado) { 
        subordinados.add(empleado); 
    }

    @Override
    public String toString() {
        return nombre + " (" + antiguedad + " años) - Equipo: " + subordinados.size();
    }
    
    @Override
    public int compareTo(Manager otro) {
        return otro.getAntiguedad() - this.getAntiguedad();
    }
}