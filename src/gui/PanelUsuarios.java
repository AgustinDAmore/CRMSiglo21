package gui;

import dao.EmpleadoDAO;
import dao.UsuarioDAO;
import modelo.Empleado;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelUsuarios extends JPanel {
    private UsuarioDAO usuarioDAO;
    private EmpleadoDAO empleadoDAO;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JComboBox<EmpleadoItem> comboEmpleados;
    private JTextField txtNombreUsuario;
    private JPasswordField txtContrasena;
    private JComboBox<String> comboRol;

    public PanelUsuarios(UsuarioDAO usuarioDAO, EmpleadoDAO empleadoDAO) {
        this.usuarioDAO = usuarioDAO;
        this.empleadoDAO = empleadoDAO;
        setLayout(new BorderLayout());
        inicializarComponentes();
        cargarUsuarios();
        cargarEmpleados();
    }

    private void inicializarComponentes() {
        // Panel del formulario para agregar/editar usuarios
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Gestionar Usuario"));

        panelFormulario.add(new JLabel("Empleado:"));
        comboEmpleados = new JComboBox<>();
        panelFormulario.add(comboEmpleados);

        panelFormulario.add(new JLabel("Nombre de Usuario:"));
        txtNombreUsuario = new JTextField();
        panelFormulario.add(txtNombreUsuario);

        panelFormulario.add(new JLabel("Contraseña:"));
        txtContrasena = new JPasswordField();
        panelFormulario.add(txtContrasena);

        panelFormulario.add(new JLabel("Rol:"));
        comboRol = new JComboBox<>(new String[]{"Administrador", "Gestor", "Empleado"});
        panelFormulario.add(comboRol);
        
        // Panel de botones del formulario
        JPanel panelBotonesFormulario = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarUsuario());
        panelBotonesFormulario.add(btnAgregar);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizarUsuario());
        panelBotonesFormulario.add(btnActualizar);
        
        panelFormulario.add(new JLabel()); // Espacio en blanco
        panelFormulario.add(panelBotonesFormulario);

        add(panelFormulario, BorderLayout.NORTH);

        // Tabla para mostrar los usuarios
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Empleado", "Nombre de Usuario", "Rol"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tablaUsuarios = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        // Listener para seleccionar una fila de la tabla y cargar datos en el formulario
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaUsuarios.getSelectedRow() != -1) {
                int filaSeleccionada = tablaUsuarios.getSelectedRow();
                String nombreEmpleado = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
                String nombreUsuario = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
                String rol = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
                
                txtNombreUsuario.setText(nombreUsuario);
                comboRol.setSelectedItem(rol);

                // Seleccionar el empleado correspondiente en el JComboBox
                for (int i = 0; i < comboEmpleados.getItemCount(); i++) {
                    if (comboEmpleados.getItemAt(i).toString().equals(nombreEmpleado)) {
                        comboEmpleados.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        // Panel de botones de acción sobre la tabla
        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.addActionListener(e -> eliminarUsuario());
        panelBotonesTabla.add(btnEliminar);
        
        JButton btnLimpiar = new JButton("Limpiar Formulario");
        btnLimpiar.addActionListener(e -> limpiarCampos());
        panelBotonesTabla.add(btnLimpiar);

        add(panelBotonesTabla, BorderLayout.SOUTH);
    }
    
    public void cargarUsuarios() {
        modeloTabla.setRowCount(0);
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        for (Usuario u : usuarios) {
            modeloTabla.addRow(new Object[]{u.getId(), u.getNombreEmpleado(), u.getNombreUsuario(), u.getRol()});
        }
    }

    private void cargarEmpleados() {
        comboEmpleados.removeAllItems();
        // CORRECCIÓN: Se agregó el bloque try-catch para manejar la excepción SQLException.
        try {
            List<Empleado> empleados = empleadoDAO.obtenerTodos();
            for (Empleado e : empleados) {
                comboEmpleados.addItem(new EmpleadoItem(e.getId(), e.getNombre()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los empleados.", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refrescarEmpleadosComboBox() {
        cargarEmpleados();
    }

    private void agregarUsuario() {
        EmpleadoItem empleadoSeleccionado = (EmpleadoItem) comboEmpleados.getSelectedItem();
        if (empleadoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idEmpleado = empleadoSeleccionado.getId();
        String nombreUsuario = txtNombreUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        String rol = (String) comboRol.getSelectedItem();

        if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario y la contraseña no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario nuevoUsuario = new Usuario(0, nombreUsuario, contrasena, rol, idEmpleado);
        usuarioDAO.crearUsuario(nuevoUsuario);
        
        JOptionPane.showMessageDialog(this, "Usuario agregado exitosamente.");
        cargarUsuarios();
        limpiarCampos();
    }

    private void actualizarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        EmpleadoItem empleadoSeleccionado = (EmpleadoItem) comboEmpleados.getSelectedItem();
        int idEmpleado = empleadoSeleccionado.getId();
        String nombreUsuario = txtNombreUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        String rol = (String) comboRol.getSelectedItem();

        if (nombreUsuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if(contrasena.isEmpty()){
            // Para no actualizar la contraseña si el campo está vacío, 
            // la lógica debería estar en el DAO o aquí se debería obtener la contraseña actual.
        }

        Usuario usuarioActualizado = new Usuario(idUsuario, nombreUsuario, contrasena, rol, idEmpleado);
        usuarioDAO.actualizarUsuario(usuarioActualizado);

        JOptionPane.showMessageDialog(this, "Usuario actualizado exitosamente.");
        cargarUsuarios();
        limpiarCampos();
    }

    private void eliminarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este usuario?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            usuarioDAO.eliminarUsuario(idUsuario);
            
            JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente.");
            cargarUsuarios();
            limpiarCampos();
        }
    }

    private void limpiarCampos() {
        txtNombreUsuario.setText("");
        txtContrasena.setText("");
        comboRol.setSelectedIndex(0);
        if (comboEmpleados.getItemCount() > 0) {
            comboEmpleados.setSelectedIndex(-1);
        }
        tablaUsuarios.clearSelection();
    }
}

