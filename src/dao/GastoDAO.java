package dao;

import modelo.Gasto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GastoDAO {
    private Connection conexion;

    public GastoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void agregarGasto(Gasto gasto) throws SQLException {
        String sql = "INSERT INTO gastos (id_proyecto, descripcion, monto, fecha) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, gasto.getIdProyecto());
            pstmt.setString(2, gasto.getDescripcion());
            pstmt.setDouble(3, gasto.getMonto());
            pstmt.setDate(4, gasto.getFecha());
            pstmt.executeUpdate();
        }
    }

    public List<Gasto> obtenerPorProyecto(int idProyecto) throws SQLException {
        List<Gasto> gastos = new ArrayList<>();
        String sql = "SELECT * FROM gastos WHERE id_proyecto = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idProyecto);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                gastos.add(new Gasto(
                    rs.getInt("id_gasto"),
                    rs.getInt("id_proyecto"),
                    rs.getString("descripcion"),
                    rs.getDouble("monto"),
                    rs.getDate("fecha")
                ));
            }
        }
        return gastos;
    }
    
    public void eliminarGasto(int id) throws SQLException {
        String sql = "DELETE FROM gastos WHERE id_gasto = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}