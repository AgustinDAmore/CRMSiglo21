package dao;

import modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private Connection conexion;

    public UsuarioDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public Usuario verificarUsuario(String nombreUsuario, String contrasena) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?"; // En un sistema real, comparar hash
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nombreUsuario);
            pstmt.setString(2, contrasena);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre_usuario"),
                    rs.getString("contrasena"),
                    rs.getString("rol"),
                    rs.getInt("id_empleado")
                );
            }
        }
        return null;
    }

    public void crearUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre_usuario, contrasena, rol, id_empleado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getRol());
            if (usuario.getIdEmpleado() == 0) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, usuario.getIdEmpleado());
            }
            pstmt.executeUpdate();
        }
    }
    
    // --- MÉTODO AÑADIDO PARA ACTUALIZAR ---
    /**
     * Actualiza los datos de un usuario.
     * Si la contraseña en el objeto usuario es nula o está vacía, no se actualiza.
     */
    public void actualizarUsuario(Usuario usuario) throws SQLException {
        // Construcción dinámica de la consulta SQL
        StringBuilder sqlBuilder = new StringBuilder("UPDATE usuarios SET rol = ?, id_empleado = ?");
        if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
            sqlBuilder.append(", contrasena = ?");
        }
        sqlBuilder.append(" WHERE id_usuario = ?");

        try (PreparedStatement pstmt = conexion.prepareStatement(sqlBuilder.toString())) {
            pstmt.setString(1, usuario.getRol());
            if (usuario.getIdEmpleado() == 0) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, usuario.getIdEmpleado());
            }

            if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
                pstmt.setString(3, usuario.getContrasena());
                pstmt.setInt(4, usuario.getId());
            } else {
                pstmt.setInt(3, usuario.getId());
            }
            pstmt.executeUpdate();
        }
    }

    public List<Usuario> obtenerTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre_usuario"),
                    rs.getString("contrasena"),
                    rs.getString("rol"),
                    rs.getInt("id_empleado")
                ));
            }
        }
        return usuarios;
    }

    public void eliminarUsuario(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.executeUpdate();
        }
    }
}