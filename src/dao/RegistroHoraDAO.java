package dao;

import modelo.RegistroHora;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroHoraDAO {
    private Connection conexion;

    public RegistroHoraDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void agregarRegistro(RegistroHora registro) throws SQLException {
        String sql = "INSERT INTO registros_horas (id_tarea, id_empleado, horas, fecha, descripcion) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, registro.getIdTarea());
            ps.setInt(2, registro.getIdEmpleado());
            ps.setDouble(3, registro.getHoras());
            // CORRECCIÓN: Se convierte java.util.Date a java.sql.Date para la base de datos.
            if (registro.getFecha() != null) {
                ps.setDate(4, new java.sql.Date(registro.getFecha().getTime()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setString(5, registro.getDescripcion());
            ps.executeUpdate();
        }
    }

    public List<RegistroHora> obtenerPorTarea(int idTarea) throws SQLException {
        List<RegistroHora> registros = new ArrayList<>();
        String sql = "SELECT * FROM registros_horas WHERE id_tarea = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idTarea);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    registros.add(new RegistroHora(
                        rs.getInt("id"),
                        rs.getInt("id_tarea"),
                        rs.getInt("id_empleado"),
                        rs.getDouble("horas"),
                        rs.getDate("fecha"), // ResultSet.getDate() devuelve un java.sql.Date que es compatible con java.util.Date
                        rs.getString("descripcion")
                    ));
                }
            }
        }
        return registros;
    }

    public void eliminarRegistro(int id) throws SQLException {
        String sql = "DELETE FROM registros_horas WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
