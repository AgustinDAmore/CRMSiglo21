package modelo;

import java.sql.Date;

public class Proyecto {
    private int id;
    private String nombre;
    private String descripcion;
    private int idCliente;
    private String estado;
    private Date fechaInicio;
    private Date fechaFin;

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
}