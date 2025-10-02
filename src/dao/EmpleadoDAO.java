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
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, empleado.getNombre());
            pstmt.setString(2, empleado.getCargo());
            pstmt.setString(3, empleado.getEmail());
            pstmt.setString(4, empleado.getTelefono());
            pstmt.executeUpdate();
        }
    }

    public List<Empleado> obtenerTodos() throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT * FROM empleados";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                empleados.add(new Empleado(
                    rs.getInt("id_empleado"),
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
        String sql = "UPDATE empleados SET nombre = ?, cargo = ?, email = ?, telefono = ? WHERE id_empleado = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, empleado.getNombre());
            pstmt.setString(2, empleado.getCargo());
            pstmt.setString(3, empleado.getEmail());
            pstmt.setString(4, empleado.getTelefono());
            pstmt.setInt(5, empleado.getId());
            pstmt.executeUpdate();
        }
    }

    public void eliminarEmpleado(int id) throws SQLException {
        String sql = "DELETE FROM empleados WHERE id_empleado = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}