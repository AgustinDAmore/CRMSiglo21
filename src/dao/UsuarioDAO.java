package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuario;

public class UsuarioDAO {
    private Connection conexion;

    public UsuarioDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public Usuario verificarUsuario(String nombreUsuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id"), // CORREGIDO
                    rs.getString("nombre_usuario"),
                    rs.getString("contrasena"),
                    rs.getString("rol"),
                    rs.getInt("id_empleado")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void crearUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (id_empleado, nombre_usuario, contrasena, rol) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, usuario.getIdEmpleado());
            ps.setString(2, usuario.getNombreUsuario());
            ps.setString(3, usuario.getContrasena());
            ps.setString(4, usuario.getRol());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, e.nombre as nombre_empleado FROM usuarios u JOIN empleados e ON u.id_empleado = e.id";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario usuario = new Usuario(
                    rs.getInt("id"), // CORREGIDO
                    rs.getString("nombre_usuario"),
                    rs.getString("contrasena"),
                    rs.getString("rol"),
                    rs.getInt("id_empleado")
                );
                usuario.setNombreEmpleado(rs.getString("nombre_empleado"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public void actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET id_empleado = ?, nombre_usuario = ?, contrasena = ?, rol = ? WHERE id = ?"; // CORREGIDO
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, usuario.getIdEmpleado());
            ps.setString(2, usuario.getNombreUsuario());
            ps.setString(3, usuario.getContrasena());
            ps.setString(4, usuario.getRol());
            ps.setInt(5, usuario.getId()); // CORREGIDO
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM usuarios WHERE id = ?"; // CORREGIDO
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

