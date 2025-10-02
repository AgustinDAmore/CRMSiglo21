package modelo;

import java.util.Date;

public class Factura {
    private int id;
    private int idProyecto;
    private String codigo;
    private double monto;
    private Date fechaEmision;
    private String estado;

    // Campo adicional para mostrar el nombre del proyecto en la GUI
    private String nombreProyecto;

    public Factura(int id, int idProyecto, String codigo, double monto, Date fechaEmision, String estado) {
        this.id = id;
        this.idProyecto = idProyecto;
        this.codigo = codigo;
        this.monto = monto;
        this.fechaEmision = fechaEmision;
        this.estado = estado;
    }

    // Getters
    public int getId() { return id; }
    public int getIdProyecto() { return idProyecto; }
    public String getCodigo() { return codigo; }
    public double getMonto() { return monto; }
    public Date getFechaEmision() { return fechaEmision; }
    public String getEstado() { return estado; }
    public String getNombreProyecto() { return nombreProyecto; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setIdProyecto(int idProyecto) { this.idProyecto = idProyecto; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setMonto(double monto) { this.monto = monto; }
    public void setFechaEmision(Date fechaEmision) { this.fechaEmision = fechaEmision; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; } // CORRECCIÓN: Método agregado
}
