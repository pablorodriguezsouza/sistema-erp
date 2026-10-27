package dominio;
import java.io.Serializable;

public class Empleado implements Serializable, Comparable<Empleado> {
    private String nombre;
    private String cedula;
    private String celular;
    private String cvTexto;
    private double salario;
    private Manager manager;
    private Area area;
    private double salarioExtra;
    private double salarioTotal;

    public Empleado(String nombre, String cedula, String celular, String cvTexto, double salario, Manager manager, Area area, double salarioExtra) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.celular = celular;
        this.cvTexto = cvTexto;
        this.salario = salario;
        this.manager = manager;
        this.area = area;
        this.salarioExtra = salarioExtra;
        this.salarioTotal = salarioExtra + salario;
    }
 
    public String getNombre() { return nombre; }
    public String getCedula() { return cedula; }
    public String getCelular() { return celular; }
    public String getCvTexto() { return cvTexto; }
    public double getSalario() { return salario; }
    public Manager getManager() { return manager; }
    public Area getArea() { return area; }
    public double getSalarioExtra() { return salarioExtra; }
    public double getSalarioTotal() { return salarioTotal; }
    
    public void setArea(Area a) { this.area = a; }

    @Override
    public String toString() {
        return nombre + " - " + cedula + " (US$ " + salario + ")";
    }

    @Override
    public int compareTo(Empleado otro) {
        return Double.compare(this.salarioTotal, otro.salarioTotal);
    }
}