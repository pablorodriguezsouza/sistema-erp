package dominio;
import java.io.Serializable;

public class Movimiento implements Serializable {
    private int mes;
    private String nombreOrigen;
    private String nombreDestino;
    private String nombreEmpleado;

    public Movimiento(int mes, String origen, String destino, String empleado) {
        this.mes = mes;
        this.nombreOrigen = origen;
        this.nombreDestino = destino;
        this.nombreEmpleado = empleado;
    }

    public int getMes() { return mes; }
    public String getNombreOrigen() { return nombreOrigen; }
    public String getNombreDestino() { return nombreDestino; }
    public String getNombreEmpleado() { return nombreEmpleado; }

    public String toCSV() {
        return mes + ";" + nombreOrigen + ";" + nombreDestino + ";" + nombreEmpleado;
    }
    
    @Override
    public String toString() {
        return "Mes " + mes + ": " + nombreEmpleado + " (" + nombreOrigen + " -> " + nombreDestino + ")";
    }
}