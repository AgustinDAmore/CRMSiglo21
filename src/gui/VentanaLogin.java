package gui;

import javax.swing.*;
import java.awt.*;
import dao.UsuarioDAO;
import modelo.Usuario;

public class VentanaLogin extends JDialog {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioAutenticado = null;

    public VentanaLogin(Frame owner, UsuarioDAO usuarioDAO) {
        super(owner, "Login", true);
        this.usuarioDAO = usuarioDAO;

        setLayout(new BorderLayout(10, 10));
        setSize(350, 180);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        panel.add(txtUsuario);

        panel.add(new JLabel("Contraseña:"));
        txtContrasena = new JPasswordField();
        panel.add(txtContrasena);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> intentarLogin());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancelar);

        panel.add(buttonPanel);

        add(panel, BorderLayout.CENTER);
    }

    private void intentarLogin() {
        String nombreUsuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());

        // CORRECCIÓN: Se eliminó el bloque try-catch innecesario
        usuarioAutenticado = usuarioDAO.verificarUsuario(nombreUsuario, contrasena);

        if (usuarioAutenticado != null) {
            JOptionPane.showMessageDialog(this, "Login exitoso.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }
}
