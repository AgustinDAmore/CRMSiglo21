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

    public void agregarProyecto(Proyecto proyecto) throws SQLException {
        String sql = "INSERT INTO proyectos (nombre, descripcion, id_cliente, estado, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, proyecto.getNombre());
            ps.setString(2, proyecto.getDescripcion());
            ps.setInt(3, proyecto.getIdCliente());
            ps.setString(4, proyecto.getEstado());
            ps.setDate(5, proyecto.getFechaInicio() != null ? new java.sql.Date(proyecto.getFechaInicio().getTime()) : null);
            ps.setDate(6, proyecto.getFechaFin() != null ? new java.sql.Date(proyecto.getFechaFin().getTime()) : null);
            ps.executeUpdate();
        }
    }

    public List<Proyecto> obtenerTodos() throws SQLException {
        List<Proyecto> proyectos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre as nombre_cliente FROM proyectos p JOIN clientes c ON p.id_cliente = c.id";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Proyecto p = new Proyecto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("id_cliente"),
                    rs.getString("estado"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin")
                );
                p.setNombreCliente(rs.getString("nombre_cliente"));
                proyectos.add(p);
            }
        }
        return proyectos;
    }
    
    public int contarProyectosActivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM proyectos WHERE estado = 'En curso'";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }


    public void actualizarProyecto(Proyecto proyecto) throws SQLException {
        String sql = "UPDATE proyectos SET nombre = ?, descripcion = ?, id_cliente = ?, estado = ?, fecha_inicio = ?, fecha_fin = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, proyecto.getNombre());
            ps.setString(2, proyecto.getDescripcion());
            ps.setInt(3, proyecto.getIdCliente());
            ps.setString(4, proyecto.getEstado());
            ps.setDate(5, proyecto.getFechaInicio() != null ? new java.sql.Date(proyecto.getFechaInicio().getTime()) : null);
            ps.setDate(6, proyecto.getFechaFin() != null ? new java.sql.Date(proyecto.getFechaFin().getTime()) : null);
            ps.setInt(7, proyecto.getId());
            ps.executeUpdate();
        }
    }

    public void eliminarProyecto(int id) throws SQLException {
        String sql = "DELETE FROM proyectos WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
