package modelo;

import java.sql.Date;

public class Tarea {
    private int id;
    private int idProyecto;
    private int idEmpleado; // Nuevo campo
    private String descripcion;
    private Date fechaLimite;
    private String estado;

    // Constructor actualizado
    public Tarea(int id, int idProyecto, int idEmpleado, String descripcion, Date fechaLimite, String estado) {
        this.id = id;
        this.idProyecto = idProyecto;
        this.idEmpleado = idEmpleado; // Asignar nuevo campo
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
    }

    // Getters
    public int getId() { return id; }
    public int getIdProyecto() { return idProyecto; }
    public int getIdEmpleado() { return idEmpleado; } // Getter para el nuevo campo
    public String getDescripcion() { return descripcion; }
    public Date getFechaLimite() { return fechaLimite; }
    public String getEstado() { return estado; }
}