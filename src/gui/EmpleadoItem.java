package gui;

public class EmpleadoItem {
    private int id;
    private String nombre;

    public EmpleadoItem(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return nombre; // Esto es lo que se mostrará en el JComboBox
    }
}