package modelo;

import java.sql.Date;

public class Gasto {
    private int id;
    private int idProyecto;
    private String descripcion;
    private double monto;
    private Date fecha;

    public Gasto(int id, int idProyecto, String descripcion, double monto, Date fecha) {
        this.id = id;
        this.idProyecto = idProyecto;
        this.descripcion = descripcion;
        this.monto = monto;
        this.fecha = fecha;
    }

    // Getters
    public int getId() { return id; }
    public int getIdProyecto() { return idProyecto; }
    public String getDescripcion() { return descripcion; }
    public double getMonto() { return monto; }
    public Date getFecha() { return fecha; }
}