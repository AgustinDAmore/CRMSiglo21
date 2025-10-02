package modelo;

public class Empleado {
    private int id;
    private String nombre;
    private String cargo;
    private String email;
    private String telefono;

    public Empleado(int id, String nombre, String cargo, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.cargo = cargo;
        this.email = email;
        this.telefono = telefono;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCargo() { return cargo; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
}