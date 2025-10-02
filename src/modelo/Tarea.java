package modelo;

import java.util.Date;

public class Tarea {
    private int id;
    private int idProyecto;
    private int idEmpleado;
    private String descripcion;
    private Date fechaLimite;
    private String estado;

    // Campos adicionales para mostrar nombres en la GUI
    private String nombreProyecto;
    private String nombreEmpleado;

    public Tarea(int id, int idProyecto, int idEmpleado, String descripcion, Date fechaLimite, String estado) {
        this.id = id;
        this.idProyecto = idProyecto;
        this.idEmpleado = idEmpleado;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
    }

    // Getters
    public int getId() { return id; }
    public int getIdProyecto() { return idProyecto; }
    public int getIdEmpleado() { return idEmpleado; }
    public String getDescripcion() { return descripcion; }
    public Date getFechaLimite() { return fechaLimite; }
    public String getEstado() { return estado; }
    public String getNombreProyecto() { return nombreProyecto; }
    public String getNombreEmpleado() { return nombreEmpleado; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setIdProyecto(int idProyecto) { this.idProyecto = idProyecto; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFechaLimite(Date fechaLimite) { this.fechaLimite = fechaLimite; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; } // CORRECCIÓN: Método agregado
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; } // CORRECCIÓN: Método agregado
}
