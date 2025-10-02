package gui;

import dao.UsuarioDAO;
import modelo.Usuario;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class VentanaLogin extends JDialog {
    private JTextField usuarioField;
    private JPasswordField contrasenaField;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioAutenticado = null;

    public VentanaLogin(Frame owner, UsuarioDAO usuarioDAO) {
        super(owner, "Inicio de Sesión", true);
        this.usuarioDAO = usuarioDAO;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        usuarioField = new JTextField(20);
        contrasenaField = new JPasswordField(20);
        JButton loginBtn = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(usuarioField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(contrasenaField, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.CENTER; add(loginBtn, gbc);

        loginBtn.addActionListener(e -> intentarLogin());

        pack();
        setLocationRelativeTo(owner);
    }

    private void intentarLogin() {
        String usuario = usuarioField.getText();
        String contrasena = new String(contrasenaField.getPassword());

        try {
            usuarioAutenticado = usuarioDAO.verificarUsuario(usuario, contrasena);
            if (usuarioAutenticado != null) {
                dispose(); // Cierra la ventana si el login es exitoso
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }
}