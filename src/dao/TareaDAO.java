package dao;

import modelo.Tarea;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO {
    private Connection conexion;

    public TareaDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void agregarTarea(Tarea tarea) throws SQLException {
        String sql = "INSERT INTO tareas (id_proyecto, id_empleado, descripcion, fecha_limite, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, tarea.getIdProyecto());
            ps.setInt(2, tarea.getIdEmpleado());
            ps.setString(3, tarea.getDescripcion());
            ps.setDate(4, tarea.getFechaLimite() != null ? new java.sql.Date(tarea.getFechaLimite().getTime()) : null);
            ps.setString(5, tarea.getEstado());
            ps.executeUpdate();
        }
    }

    public List<Tarea> obtenerTodos() throws SQLException {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT t.*, p.nombre as nombre_proyecto, e.nombre as nombre_empleado " +
                     "FROM tareas t " +
                     "JOIN proyectos p ON t.id_proyecto = p.id " +
                     "JOIN empleados e ON t.id_empleado = e.id";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Tarea t = new Tarea(
                    rs.getInt("id"),
                    rs.getInt("id_proyecto"),
                    rs.getInt("id_empleado"),
                    rs.getString("descripcion"),
                    rs.getDate("fecha_limite"),
                    rs.getString("estado")
                );
                t.setNombreProyecto(rs.getString("nombre_proyecto"));
                t.setNombreEmpleado(rs.getString("nombre_empleado"));
                tareas.add(t);
            }
        }
        return tareas;
    }

    public void actualizarTarea(Tarea tarea) throws SQLException {
        String sql = "UPDATE tareas SET id_proyecto = ?, id_empleado = ?, descripcion = ?, fecha_limite = ?, estado = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, tarea.getIdProyecto());
            ps.setInt(2, tarea.getIdEmpleado());
            ps.setString(3, tarea.getDescripcion());
            ps.setDate(4, tarea.getFechaLimite() != null ? new java.sql.Date(tarea.getFechaLimite().getTime()) : null);
            ps.setString(5, tarea.getEstado());
            ps.setInt(6, tarea.getId());
            ps.executeUpdate();
        }
    }

    public void eliminarTarea(int id) throws SQLException {
        String sql = "DELETE FROM tareas WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    public int contarTareasPorVencer() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tareas WHERE estado != 'Completada' AND fecha_limite BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
