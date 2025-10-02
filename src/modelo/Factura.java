package modelo;

import java.sql.Date;

public class Factura {
    private int id;
    private int idProyecto;
    private String codigo; // <-- CAMPO AÑADIDO
    private double monto;
    private Date fechaEmision;
    private String estado; // "Pagada", "Pendiente", "Vencida"

    // Constructor actualizado
    public Factura(int id, int idProyecto, String codigo, double monto, Date fechaEmision, String estado) {
        this.id = id;
        this.idProyecto = idProyecto;
        this.codigo = codigo; // <-- ASIGNACIÓN
        this.monto = monto;
        this.fechaEmision = fechaEmision;
        this.estado = estado;
    }

    // Getters
    public int getId() { return id; }
    public int getIdProyecto() { return idProyecto; }
    public String getCodigo() { return codigo; } // <-- GETTER NUEVO
    public double getMonto() { return monto; }
    public Date getFechaEmision() { return fechaEmision; }
    public String getEstado() { return estado; }
}