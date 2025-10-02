package gui;

import com.toedter.calendar.JDateChooser;
import dao.GastoDAO;
import dao.ProyectoDAO;
import modelo.Gasto;
import modelo.Proyecto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class PanelGastos extends JPanel {

    private GastoDAO gastoDAO;
    private ProyectoDAO proyectoDAO;
    private JTable gastosTable;
    private DefaultTableModel tableModel;

    private JComboBox<ProyectoItem> proyectoComboBox;
    private JTextField descripcionField;
    private JTextField montoField;
    private JDateChooser fechaChooser;

    public PanelGastos(GastoDAO gastoDAO, ProyectoDAO proyectoDAO) {
        this.gastoDAO = gastoDAO;
        this.proyectoDAO = proyectoDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel superior con controles de entrada
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Registrar Nuevo Gasto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        proyectoComboBox = new JComboBox<>();
        descripcionField = new JTextField(20);
        montoField = new JTextField(10);
        fechaChooser = new JDateChooser();
        fechaChooser.setDateFormatString("yyyy-MM-dd");
        fechaChooser.setDate(new java.util.Date()); // Fecha por defecto es hoy

        // Fila 1: Proyecto
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Asociar a Proyecto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; inputPanel.add(proyectoComboBox, gbc);

        // Fila 2: Descripción, Monto y Fecha
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(descripcionField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; inputPanel.add(new JLabel("Monto:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; inputPanel.add(montoField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(fechaChooser, gbc);

        JButton addBtn = new JButton("Agregar Gasto");
        gbc.gridx = 3; gbc.gridy = 2; inputPanel.add(addBtn, gbc);
        
        topPanel.add(inputPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Tabla para mostrar los gastos
        tableModel = new DefaultTableModel(new String[]{"ID", "Descripción", "Monto", "Fecha"}, 0);
        gastosTable = new JTable(tableModel);
        add(new JScrollPane(gastosTable), BorderLayout.CENTER);
        
        // Panel de botones inferior
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteBtn = new JButton("Eliminar Gasto Seleccionado");
        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- LISTENERS ---
        proyectoComboBox.addActionListener(e -> cargarGastosDelProyecto());
        addBtn.addActionListener(e -> agregarGasto());
        deleteBtn.addActionListener(e -> eliminarGasto());

        refrescarProyectosComboBox();
    }

    public void refrescarProyectosComboBox() {
        try {
            List<Proyecto> proyectos = proyectoDAO.obtenerTodos();
            proyectoComboBox.removeAllItems();
            proyectoComboBox.addItem(new ProyectoItem(0, "Seleccione un proyecto..."));
            for (Proyecto proyecto : proyectos) {
                proyectoComboBox.addItem(new ProyectoItem(proyecto.getId(), proyecto.getNombre()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proyectos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarGastosDelProyecto() {
        tableModel.setRowCount(0);
        ProyectoItem selected = (ProyectoItem) proyectoComboBox.getSelectedItem();
        if (selected == null || selected.getId() == 0) {
            return;
        }

        try {
            List<Gasto> gastos = gastoDAO.obtenerPorProyecto(selected.getId());
            for (Gasto gasto : gastos) {
                tableModel.addRow(new Object[]{
                    gasto.getId(),
                    gasto.getDescripcion(),
                    gasto.getMonto(),
                    gasto.getFecha()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar gastos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarGasto() {
        ProyectoItem selected = (ProyectoItem) proyectoComboBox.getSelectedItem();
        if (selected == null || selected.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proyecto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (descripcionField.getText().trim().isEmpty() || montoField.getText().trim().isEmpty() || fechaChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String descripcion = descripcionField.getText();
            double monto = Double.parseDouble(montoField.getText());
            Date fecha = new Date(fechaChooser.getDate().getTime());

            Gasto nuevoGasto = new Gasto(0, selected.getId(), descripcion, monto, fecha);
            gastoDAO.agregarGasto(nuevoGasto);
            
            JOptionPane.showMessageDialog(this, "Gasto agregado con éxito.");
            descripcionField.setText("");
            montoField.setText("");
            cargarGastosDelProyecto(); // Recargar la tabla

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El monto debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar el gasto: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarGasto() {
        int selectedRow = gastosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un gasto para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este gasto?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int idGasto = (int) gastosTable.getValueAt(selectedRow, 0);
                gastoDAO.eliminarGasto(idGasto);
                cargarGastosDelProyecto(); // Recargar la tabla
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el gasto: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}