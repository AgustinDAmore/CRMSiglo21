package dao;

import modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private Connection conexion;

    public ClienteDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void agregarCliente(Cliente cliente) throws SQLException {
        // Se añaden los nuevos campos al INSERT
        String sql = "INSERT INTO clientes (nombre, empresa, email, telefono, estado, notas) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getEmpresa());
            pstmt.setString(3, cliente.getEmail());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getEstado());
            pstmt.setString(6, cliente.getNotas());
            pstmt.executeUpdate();
        }
    }

    public List<Cliente> obtenerTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Se llama al constructor correcto con todos los campos
                clientes.add(new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("nombre"),
                    rs.getString("empresa"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getString("estado"),
                    rs.getString("notas")
                ));
            }
        }
        return clientes;
    }

    public void actualizarCliente(Cliente cliente) throws SQLException {
        // Se añaden los nuevos campos al UPDATE
        String sql = "UPDATE clientes SET nombre = ?, empresa = ?, email = ?, telefono = ?, estado = ?, notas = ? WHERE id_cliente = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getEmpresa());
            pstmt.setString(3, cliente.getEmail());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getEstado());
            pstmt.setString(6, cliente.getNotas());
            pstmt.setInt(7, cliente.getId());
            pstmt.executeUpdate();
        }
    }

    public void eliminarCliente(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}