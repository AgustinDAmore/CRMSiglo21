package modelo;

// CORRECCIÓN: Se cambió el import a java.util.Date para que el modelo sea más genérico.
import java.util.Date;

public class RegistroHora {
    private int id;
    private int idTarea;
    private int idEmpleado;
    private double horas;
    private Date fecha;
    private String descripcion;

    public RegistroHora(int id, int idTarea, int idEmpleado, double horas, Date fecha, String descripcion) {
        this.id = id;
        this.idTarea = idTarea;
        this.idEmpleado = idEmpleado;
        this.horas = horas;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    // Getters
    public int getId() { return id; }
    public int getIdTarea() { return idTarea; }
    public int getIdEmpleado() { return idEmpleado; }
    public double getHoras() { return horas; }
    public Date getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setIdTarea(int idTarea) { this.idTarea = idTarea; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }
    public void setHoras(double horas) { this.horas = horas; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
