package modelo;

public class Cliente {
    private int id;
    private String nombre;
    private String empresa;
    private String email;
    private String telefono;
    private String estado; // "Activo", "Inactivo", "Potencial"
    private String notas;

    public Cliente(int id, String nombre, String empresa, String email, String telefono, String estado, String notas) {
        this.id = id;
        this.nombre = nombre;
        this.empresa = empresa;
        this.email = email;
        this.telefono = telefono;
        this.estado = estado;
        this.notas = notas;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmpresa() { return empresa; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getEstado() { return estado; }
    public String getNotas() { return notas; }
}