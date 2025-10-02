package dao;

import modelo.Proyecto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProyectoDAO {
    private Connection conexion;

    public ProyectoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public int contarProyectosActivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM proyectos WHERE estado = 'En curso'";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public void agregarProyecto(Proyecto proyecto) throws SQLException {
        String sql = "INSERT INTO proyectos (nombre_proyecto, descripcion, id_cliente, estado, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, proyecto.getNombre());
            pstmt.setString(2, proyecto.getDescripcion());
            pstmt.setInt(3, proyecto.getIdCliente());
            pstmt.setString(4, proyecto.getEstado());
            pstmt.setDate(5, proyecto.getFechaInicio());
            pstmt.setDate(6, proyecto.getFechaFin());
            pstmt.executeUpdate();
        }
    }

    public List<Proyecto> obtenerTodos() throws SQLException {
        List<Proyecto> proyectos = new ArrayList<>();
        String sql = "SELECT * FROM proyectos";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Se llama al constructor correcto con todos los campos
                proyectos.add(new Proyecto(
                    rs.getInt("id_proyecto"),
                    rs.getString("nombre_proyecto"),
                    rs.getString("descripcion"),
                    rs.getInt("id_cliente"),
                    rs.getString("estado"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin")
                ));
            }
        }
        return proyectos;
    }

    public void actualizarProyecto(Proyecto proyecto) throws SQLException {
        String sql = "UPDATE proyectos SET nombre_proyecto = ?, descripcion = ?, id_cliente = ?, estado = ?, fecha_inicio = ?, fecha_fin = ? WHERE id_proyecto = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, proyecto.getNombre());
            pstmt.setString(2, proyecto.getDescripcion());
            pstmt.setInt(3, proyecto.getIdCliente());
            pstmt.setString(4, proyecto.getEstado());
            pstmt.setDate(5, proyecto.getFechaInicio());
            pstmt.setDate(6, proyecto.getFechaFin());
            pstmt.setInt(7, proyecto.getId());
            pstmt.executeUpdate();
        }
    }

    public void eliminarProyecto(int id) throws SQLException {
        String sql = "DELETE FROM proyectos WHERE id_proyecto = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}