package modelo;

import java.util.Date;

public class Proyecto {
    private int id;
    private String nombre;
    private String descripcion;
    private int idCliente;
    private String estado;
    private Date fechaInicio;
    private Date fechaFin;

    // Campo adicional para mostrar el nombre del cliente en la GUI
    private String nombreCliente;

    public Proyecto(int id, String nombre, String descripcion, int idCliente, String estado, Date fechaInicio, Date fechaFin) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idCliente = idCliente;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getIdCliente() { return idCliente; }
    public String getEstado() { return estado; }
    public Date getFechaInicio() { return fechaInicio; }
    public Date getFechaFin() { return fechaFin; }
    public String getNombreCliente() { return nombreCliente; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
}

