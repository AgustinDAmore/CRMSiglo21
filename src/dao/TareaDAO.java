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
        // Se añade el nuevo campo al INSERT
        String sql = "INSERT INTO tareas (id_proyecto, id_empleado, descripcion, fecha_limite, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, tarea.getIdProyecto());
            pstmt.setInt(2, tarea.getIdEmpleado()); // Añadir id_empleado
            pstmt.setString(3, tarea.getDescripcion());
            pstmt.setDate(4, tarea.getFechaLimite());
            pstmt.setString(5, tarea.getEstado());
            pstmt.executeUpdate();
        }
    }

    public List<Tarea> obtenerTodas() throws SQLException {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT * FROM tareas";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Se llama al constructor actualizado
                tareas.add(new Tarea(
                    rs.getInt("id_tarea"),
                    rs.getInt("id_proyecto"),
                    rs.getInt("id_empleado"), // Obtener id_empleado
                    rs.getString("descripcion"),
                    rs.getDate("fecha_limite"),
                    rs.getString("estado")
                ));
            }
        }
        return tareas;
    }

    public void actualizarTarea(Tarea tarea) throws SQLException {
        // Se añade el nuevo campo al UPDATE
        String sql = "UPDATE tareas SET id_proyecto = ?, id_empleado = ?, descripcion = ?, fecha_limite = ?, estado = ? WHERE id_tarea = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, tarea.getIdProyecto());
            pstmt.setInt(2, tarea.getIdEmpleado()); // Actualizar id_empleado
            pstmt.setString(3, tarea.getDescripcion());
            pstmt.setDate(4, tarea.getFechaLimite());
            pstmt.setString(5, tarea.getEstado());
            pstmt.setInt(6, tarea.getId());
            pstmt.executeUpdate();
        }
    }

    public void eliminarTarea(int id) throws SQLException {
        String sql = "DELETE FROM tareas WHERE id_tarea = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public int contarTareasPorVencer() throws SQLException {
        // Cuenta tareas pendientes cuya fecha límite es en los próximos 7 días
        String sql = "SELECT COUNT(*) FROM tareas WHERE estado = 'Pendiente' AND fecha_limite BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}