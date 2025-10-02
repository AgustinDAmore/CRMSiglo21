package modelo;

public class Usuario {
    private int id;
    private String nombreUsuario;
    private String contrasena;
    private String rol;
    private int idEmpleado;
    private String nombreEmpleado; // Campo para almacenar el nombre del empleado asociado

    public Usuario(int id, String nombreUsuario, String contrasena, String rol, int idEmpleado) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.idEmpleado = idEmpleado;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getRol() {
        return rol;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }
}
