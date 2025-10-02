package dao;

import modelo.Factura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO {
    private Connection conexion;

    public FacturaDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void agregarFactura(Factura factura) throws SQLException {
        // Incluye el nuevo campo codigo_factura
        String sql = "INSERT INTO facturas (id_proyecto, codigo_factura, monto, fecha_emision, estado) VALUES (?, ?, ?, CURDATE(), ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, factura.getIdProyecto());
            pstmt.setString(2, factura.getCodigo());
            pstmt.setDouble(3, factura.getMonto());
            pstmt.setString(4, factura.getEstado());
            pstmt.executeUpdate();
        }
    }

    public List<Factura> obtenerTodos() throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM facturas";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Incluye el nuevo campo codigo_factura
                facturas.add(new Factura(
                    rs.getInt("id_factura"),
                    rs.getInt("id_proyecto"),
                    rs.getString("codigo_factura"),
                    rs.getDouble("monto"),
                    rs.getDate("fecha_emision"),
                    rs.getString("estado")
                ));
            }
        }
        return facturas;
    }
    
    public void actualizarEstadoFactura(int id, String estado) throws SQLException {
        String sql = "UPDATE facturas SET estado = ? WHERE id_factura = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, estado);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    public void eliminarFactura(int id) throws SQLException {
        String sql = "DELETE FROM facturas WHERE id_factura = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // --- MÉTODOS DEL DASHBOARD QUE FALTABAN ---

    public int contarFacturasPendientes() throws SQLException {
        String sql = "SELECT COUNT(*) FROM facturas WHERE estado = 'Pendiente'";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public double sumarIngresosDelMes() throws SQLException {
        String sql = "SELECT SUM(monto) FROM facturas WHERE estado = 'Pagada' AND MONTH(fecha_emision) = MONTH(CURDATE()) AND YEAR(fecha_emision) = YEAR(CURDATE())";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}