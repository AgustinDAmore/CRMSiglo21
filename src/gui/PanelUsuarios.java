package gui;

import dao.EmpleadoDAO;
import dao.UsuarioDAO;
import modelo.Empleado;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class PanelUsuarios extends JPanel {

    private UsuarioDAO usuarioDAO;
    private EmpleadoDAO empleadoDAO;

    private JTable usuariosTable;
    private DefaultTableModel tableModel;

    private JTextField nombreUsuarioField;
    private JPasswordField contrasenaField;
    private JComboBox<String> rolComboBox;
    private JComboBox<EmpleadoItem> empleadoComboBox;
    
    private JButton addBtn, updateBtn, clearBtn;

    public PanelUsuarios(UsuarioDAO usuarioDAO, EmpleadoDAO empleadoDAO) {
        this.usuarioDAO = usuarioDAO;
        this.empleadoDAO = empleadoDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de entrada
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nombreUsuarioField = new JTextField(20);
        contrasenaField = new JPasswordField(20);
        rolComboBox = new JComboBox<>(new String[]{"Administrador", "Gestor", "Empleado"});
        empleadoComboBox = new JComboBox<>();
        
        contrasenaField.setToolTipText("Dejar en blanco para no cambiar la contraseña");

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Nombre de Usuario:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(nombreUsuarioField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(contrasenaField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(rolComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Vincular a Empleado:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; inputPanel.add(empleadoComboBox, gbc);

        // Panel de botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addBtn = new JButton("Crear Usuario");
        updateBtn = new JButton("Actualizar Usuario");
        clearBtn = new JButton("Limpiar");
        actionPanel.add(addBtn);
        actionPanel.add(updateBtn);
        actionPanel.add(clearBtn);

        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(actionPanel, gbc);
        
        add(inputPanel, BorderLayout.NORTH);

        // Tabla de usuarios
        tableModel = new DefaultTableModel(new String[]{"ID Usuario", "Nombre Usuario", "Rol", "ID Empleado Vinculado"}, 0) {
             @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que la tabla no sea editable
            }
        };
        usuariosTable = new JTable(tableModel);
        add(new JScrollPane(usuariosTable), BorderLayout.CENTER);

        // Panel de botones inferior
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteBtn = new JButton("Eliminar Usuario Seleccionado");
        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        addBtn.addActionListener(e -> crearUsuario());
        updateBtn.addActionListener(e -> actualizarUsuario());
        clearBtn.addActionListener(e -> limpiarCampos());
        deleteBtn.addActionListener(e -> eliminarUsuario());
        
        usuariosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && usuariosTable.getSelectedRow() != -1) {
                cargarDatosUsuarioSeleccionado();
            }
        });

        refrescarEmpleadosComboBox();
        cargarUsuarios();
        limpiarCampos();
    }

    public void refrescarEmpleadosComboBox() {
         new SwingWorker<Vector<EmpleadoItem>, Void>() {
            @Override
            protected Vector<EmpleadoItem> doInBackground() throws Exception {
                List<Empleado> empleados = empleadoDAO.obtenerTodos();
                Vector<EmpleadoItem> items = new Vector<>();
                items.add(new EmpleadoItem(0, "Ninguno"));
                for (Empleado empleado : empleados) {
                    items.add(new EmpleadoItem(empleado.getId(), empleado.getNombre()));
                }
                return items;
            }
             @Override
            protected void done() {
                try {
                    empleadoComboBox.setModel(new DefaultComboBoxModel<>(get()));
                } catch (Exception e) {
                   JOptionPane.showMessageDialog(PanelUsuarios.this, "Error al cargar empleados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    private void cargarUsuarios() {
        tableModel.setRowCount(0);
        new SwingWorker<List<Usuario>, Void>() {
            @Override
            protected List<Usuario> doInBackground() throws Exception {
                return usuarioDAO.obtenerTodos();
            }

            @Override
            protected void done() {
                try {
                    for (Usuario usuario : get()) {
                        tableModel.addRow(new Object[]{
                            usuario.getId(),
                            usuario.getNombreUsuario(),
                            usuario.getRol(),
                            usuario.getIdEmpleado() == 0 ? "N/A" : usuario.getIdEmpleado()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelUsuarios.this, "Error al cargar usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    private void cargarDatosUsuarioSeleccionado() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow == -1) return;

        nombreUsuarioField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        nombreUsuarioField.setEditable(false);
        contrasenaField.setText("");
        
        rolComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());

        Object idEmpleadoObj = tableModel.getValueAt(selectedRow, 3);
        int idEmpleado = (idEmpleadoObj.equals("N/A")) ? 0 : Integer.parseInt(idEmpleadoObj.toString());
        
        for (int i = 0; i < empleadoComboBox.getItemCount(); i++) {
            if (empleadoComboBox.getItemAt(i).getId() == idEmpleado) {
                empleadoComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        addBtn.setEnabled(false);
        updateBtn.setEnabled(true);
    }

    private void crearUsuario() {
        String nombre = nombreUsuarioField.getText();
        String contrasena = new String(contrasenaField.getPassword());
        if (nombre.trim().isEmpty() || contrasena.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario y la contraseña son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String rol = (String) rolComboBox.getSelectedItem();
            EmpleadoItem empleadoSel = (EmpleadoItem) empleadoComboBox.getSelectedItem();
            int idEmpleado = (empleadoSel != null) ? empleadoSel.getId() : 0;

            Usuario nuevoUsuario = new Usuario(0, nombre, contrasena, rol, idEmpleado);
            usuarioDAO.crearUsuario(nuevoUsuario);

            JOptionPane.showMessageDialog(this, "Usuario creado con éxito.");
            limpiarCampos();
            cargarUsuarios();
        } catch (SQLException e) {
            if(e.getMessage().contains("UNIQUE constraint failed") || e.getMessage().contains("Duplicate entry")) {
                 JOptionPane.showMessageDialog(this, "Error: El nombre de usuario o el empleado ya tienen una cuenta.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Error al crear el usuario: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void actualizarUsuario() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idUsuario = (int) tableModel.getValueAt(selectedRow, 0);
            String nombreUsuario = nombreUsuarioField.getText();
            String contrasena = new String(contrasenaField.getPassword());
            String rol = (String) rolComboBox.getSelectedItem();
            EmpleadoItem empleadoSel = (EmpleadoItem) empleadoComboBox.getSelectedItem();
            int idEmpleado = (empleadoSel != null) ? empleadoSel.getId() : 0;
            
            Usuario usuarioActualizado = new Usuario(idUsuario, nombreUsuario, contrasena, rol, idEmpleado);
            usuarioDAO.actualizarUsuario(usuarioActualizado);

            JOptionPane.showMessageDialog(this, "Usuario actualizado con éxito.");
            limpiarCampos();
            cargarUsuarios();

        } catch (SQLException e) {
             if(e.getMessage().contains("UNIQUE constraint failed") || e.getMessage().contains("Duplicate entry")) {
                 JOptionPane.showMessageDialog(this, "Error: El empleado seleccionado ya está asignado a otro usuario.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el usuario: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarUsuario() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idUsuario = (int) usuariosTable.getValueAt(selectedRow, 0);
        if (idUsuario == 1) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar al usuario administrador principal.", "Acción no permitida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este usuario?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                usuarioDAO.eliminarUsuario(idUsuario);
                limpiarCampos();
                cargarUsuarios();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el usuario: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarCampos() {
        nombreUsuarioField.setText("");
        contrasenaField.setText("");
        rolComboBox.setSelectedIndex(0);
        // --- FIX ---
        // Solo intenta seleccionar un índice si el ComboBox tiene elementos.
        if (empleadoComboBox.getItemCount() > 0) {
            empleadoComboBox.setSelectedIndex(0);
        }
        // --- END FIX ---
        usuariosTable.clearSelection();
        
        nombreUsuarioField.setEditable(true);
        addBtn.setEnabled(true);
        updateBtn.setEnabled(false);
    }
}