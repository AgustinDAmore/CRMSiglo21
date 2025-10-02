package dao;

import modelo.RegistroHora;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD de los registros de horas.
 */
public class RegistroHoraDAO {
    private Connection conexion;

    public RegistroHoraDAO(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * Agrega un nuevo registro de horas a la base de datos.
     */
    public void agregarRegistro(RegistroHora registro) throws SQLException {
        String sql = "INSERT INTO registros_horas (id_tarea, id_empleado, horas, fecha, descripcion) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, registro.getIdTarea());
            pstmt.setInt(2, registro.getIdEmpleado());
            pstmt.setDouble(3, registro.getHoras());
            pstmt.setDate(4, registro.getFecha());
            pstmt.setString(5, registro.getDescripcion());
            pstmt.executeUpdate();
        }
    }

    /**
     * Obtiene todos los registros de horas asociados a una tarea específica.
     */
    public List<RegistroHora> obtenerPorTarea(int idTarea) throws SQLException {
        List<RegistroHora> registros = new ArrayList<>();
        String sql = "SELECT * FROM registros_horas WHERE id_tarea = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idTarea);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                registros.add(new RegistroHora(
                    rs.getInt("id_registro"),
                    rs.getInt("id_tarea"),
                    rs.getInt("id_empleado"),
                    rs.getDouble("horas"),
                    rs.getDate("fecha"),
                    rs.getString("descripcion")
                ));
            }
        }
        return registros;
    }

    /**
     * Elimina un registro de horas de la base de datos.
     */
    public void eliminarRegistro(int id) throws SQLException {
        String sql = "DELETE FROM registros_horas WHERE id_registro = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}