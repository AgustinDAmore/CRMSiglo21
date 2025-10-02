package gui;

public class ProyectoItem {
    private int id;
    private String nombre;

    public ProyectoItem(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    // Esto es lo que se mostrará en el menú desplegable
    @Override
    public String toString() {
        return nombre;
    }
}