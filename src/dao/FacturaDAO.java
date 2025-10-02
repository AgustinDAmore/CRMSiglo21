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
        String sql = "INSERT INTO facturas (id_proyecto, codigo, monto, fecha_emision, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, factura.getIdProyecto());
            ps.setString(2, factura.getCodigo());
            ps.setDouble(3, factura.getMonto());
            ps.setDate(4, new java.sql.Date(factura.getFechaEmision().getTime()));
            ps.setString(5, factura.getEstado());
            ps.executeUpdate();
        }
    }

    public List<Factura> obtenerTodos() throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT f.*, p.nombre as nombre_proyecto FROM facturas f JOIN proyectos p ON f.id_proyecto = p.id";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Factura f = new Factura(
                    rs.getInt("id"),
                    rs.getInt("id_proyecto"),
                    rs.getString("codigo"),
                    rs.getDouble("monto"),
                    rs.getDate("fecha_emision"),
                    rs.getString("estado")
                );
                f.setNombreProyecto(rs.getString("nombre_proyecto"));
                facturas.add(f);
            }
        }
        return facturas;
    }

    public void actualizarEstadoFactura(int idFactura, String nuevoEstado) throws SQLException {
        String sql = "UPDATE facturas SET estado = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idFactura);
            ps.executeUpdate();
        }
    }

    public void eliminarFactura(int id) throws SQLException {
        String sql = "DELETE FROM facturas WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    public int contarFacturasPendientes() throws SQLException {
        String sql = "SELECT COUNT(*) FROM facturas WHERE estado = 'Pendiente'";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public double sumarIngresosDelMes() throws SQLException {
        String sql = "SELECT SUM(monto) FROM facturas WHERE estado = 'Pagada' AND MONTH(fecha_emision) = MONTH(CURDATE()) AND YEAR(fecha_emision) = YEAR(CURDATE())";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}
