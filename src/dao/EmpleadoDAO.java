package dao;

import modelo.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {
    private Connection conexion;

    public EmpleadoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void agregarEmpleado(Empleado empleado) throws SQLException {
        String sql = "INSERT INTO empleados (nombre, cargo, email, telefono) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getCargo());
            ps.setString(3, empleado.getEmail());
            ps.setString(4, empleado.getTelefono());
            ps.executeUpdate();
        }
    }

    public List<Empleado> obtenerTodos() throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT * FROM empleados";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                empleados.add(new Empleado(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("cargo"),
                    rs.getString("email"),
                    rs.getString("telefono")
                ));
            }
        }
        return empleados;
    }

    public void actualizarEmpleado(Empleado empleado) throws SQLException {
        String sql = "UPDATE empleados SET nombre = ?, cargo = ?, email = ?, telefono = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getCargo());
            ps.setString(3, empleado.getEmail());
            ps.setString(4, empleado.getTelefono());
            ps.setInt(5, empleado.getId());
            ps.executeUpdate();
        }
    }

    public void eliminarEmpleado(int id) throws SQLException {
        String sql = "DELETE FROM empleados WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
