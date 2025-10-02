package gui;

import dao.EmpleadoDAO;
import modelo.Empleado;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelEmpleados extends JPanel {

    private EmpleadoDAO empleadoDAO;
    private JTable empleadosTable;
    private DefaultTableModel tableModel;
    private JTextField nombreField, cargoField, emailField, telefonoField;

    public PanelEmpleados(EmpleadoDAO empleadoDAO) {
        this.empleadoDAO = empleadoDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Cargo", "Email", "Teléfono"}, 0);
        empleadosTable = new JTable(tableModel);
        add(new JScrollPane(empleadosTable), BorderLayout.CENTER);

        // Panel de entrada
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Datos del Empleado"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nombreField = new JTextField(20);
        cargoField = new JTextField(20);
        emailField = new JTextField(20);
        telefonoField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(nombreField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Cargo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(cargoField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; inputPanel.add(telefonoField, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addBtn = new JButton("Agregar");
        JButton updateBtn = new JButton("Actualizar");
        JButton deleteBtn = new JButton("Eliminar");
        JButton clearBtn = new JButton("Limpiar");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        addBtn.addActionListener(e -> agregarEmpleado());
        updateBtn.addActionListener(e -> actualizarEmpleado());
        deleteBtn.addActionListener(e -> eliminarEmpleado());
        clearBtn.addActionListener(e -> limpiarCampos());

        empleadosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && empleadosTable.getSelectedRow() != -1) {
                int fila = empleadosTable.getSelectedRow();
                nombreField.setText(tableModel.getValueAt(fila, 1).toString());
                cargoField.setText(tableModel.getValueAt(fila, 2).toString());
                emailField.setText(tableModel.getValueAt(fila, 3) != null ? tableModel.getValueAt(fila, 3).toString() : "");
                telefonoField.setText(tableModel.getValueAt(fila, 4) != null ? tableModel.getValueAt(fila, 4).toString() : "");
            }
        });

        cargarEmpleados();
    }

    private void cargarEmpleados() {
        tableModel.setRowCount(0);
        try {
            List<Empleado> empleados = empleadoDAO.obtenerTodos();
            for (Empleado empleado : empleados) {
                tableModel.addRow(new Object[]{
                    empleado.getId(),
                    empleado.getNombre(),
                    empleado.getCargo(),
                    empleado.getEmail(),
                    empleado.getTelefono()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar empleados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarEmpleado() {
        try {
            Empleado nuevoEmpleado = new Empleado(0,
                nombreField.getText(),
                cargoField.getText(),
                emailField.getText(),
                telefonoField.getText());
            empleadoDAO.agregarEmpleado(nuevoEmpleado);
            JOptionPane.showMessageDialog(this, "Empleado agregado con éxito.");
            limpiarCampos();
            cargarEmpleados();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEmpleado() {
        int fila = empleadosTable.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(fila, 0);
            Empleado empleado = new Empleado(id,
                nombreField.getText(),
                cargoField.getText(),
                emailField.getText(),
                telefonoField.getText());
            empleadoDAO.actualizarEmpleado(empleado);
            JOptionPane.showMessageDialog(this, "Empleado actualizado con éxito.");
            limpiarCampos();
            cargarEmpleados();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEmpleado() {
        int fila = empleadosTable.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(fila, 0);
            empleadoDAO.eliminarEmpleado(id);
            JOptionPane.showMessageDialog(this, "Empleado eliminado con éxito.");
            limpiarCampos();
            cargarEmpleados();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        nombreField.setText("");
        cargoField.setText("");
        emailField.setText("");
        telefonoField.setText("");
        empleadosTable.clearSelection();
    }
}