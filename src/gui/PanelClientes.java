package gui;

import dao.ClienteDAO;
import modelo.Cliente;
import util.Exportador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

public class PanelClientes extends JPanel {

    private ClienteDAO clienteDAO;
    private JTable clientesTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter; // Sorter para filtrar
    private JTextField nombreClienteField, empresaClienteField, emailClienteField, telefonoClienteField, filtroField;
    private JComboBox<String> estadoClienteComboBox;
    private JTextArea notasClienteArea;

    public PanelClientes(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Panel de Búsqueda ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar Cliente:"));
        filtroField = new JTextField(25);
        searchPanel.add(filtroField);

        // --- Tabla ---
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Empresa", "Email", "Teléfono", "Estado"}, 0);
        clientesTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        clientesTable.setRowSorter(sorter);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(clientesTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        // Panel de entrada
        JPanel clientesInputPanel = new JPanel(new GridBagLayout());
        clientesInputPanel.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nombreClienteField = new JTextField(20);
        empresaClienteField = new JTextField(20);
        emailClienteField = new JTextField(20);
        telefonoClienteField = new JTextField(20);
        estadoClienteComboBox = new JComboBox<>(new String[]{"Activo", "Inactivo", "Potencial"});
        notasClienteArea = new JTextArea(4, 20);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        clientesInputPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        clientesInputPanel.add(nombreClienteField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        clientesInputPanel.add(new JLabel("Empresa:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        clientesInputPanel.add(empresaClienteField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        clientesInputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        clientesInputPanel.add(emailClienteField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        clientesInputPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        clientesInputPanel.add(telefonoClienteField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        clientesInputPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        clientesInputPanel.add(estadoClienteComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.NORTHEAST; gbc.weightx = 0;
        clientesInputPanel.add(new JLabel("Notas:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        clientesInputPanel.add(new JScrollPane(notasClienteArea), gbc);

        add(clientesInputPanel, BorderLayout.NORTH);

        // Paneles de botones
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addClienteBtn = new JButton("Agregar Cliente");
        JButton updateClienteBtn = new JButton("Actualizar Cliente");
        JButton deleteClienteBtn = new JButton("Eliminar Cliente");
        JButton clearClienteBtn = new JButton("Limpiar Campos");
        actionButtonPanel.add(addClienteBtn);
        actionButtonPanel.add(updateClienteBtn);
        actionButtonPanel.add(deleteClienteBtn);
        actionButtonPanel.add(clearClienteBtn);

        JPanel exportButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportButtonPanel.setBorder(BorderFactory.createTitledBorder("Opciones de Exportación"));
        JButton exportCsvBtn = new JButton("Exportar a CSV");
        JButton exportPdfBtn = new JButton("Exportar a PDF");
        exportButtonPanel.add(exportCsvBtn);
        exportButtonPanel.add(exportPdfBtn);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(actionButtonPanel, BorderLayout.CENTER);
        southPanel.add(exportButtonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Listeners
        addClienteBtn.addActionListener(e -> agregarCliente());
        updateClienteBtn.addActionListener(e -> actualizarCliente());
        deleteClienteBtn.addActionListener(e -> eliminarCliente());
        clearClienteBtn.addActionListener(e -> limpiarCampos());
        exportCsvBtn.addActionListener(e -> Exportador.exportarACSV(clientesTable, this));
        exportPdfBtn.addActionListener(e -> Exportador.exportarAPDF(clientesTable, this, "Reporte de Clientes"));

        filtroField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = filtroField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    // Filtra por cualquier columna que contenga el texto (insensible a mayúsculas)
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        clientesTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && clientesTable.getSelectedRow() != -1) {
                int modelRow = clientesTable.convertRowIndexToModel(clientesTable.getSelectedRow());
                nombreClienteField.setText(tableModel.getValueAt(modelRow, 1).toString());
                empresaClienteField.setText(tableModel.getValueAt(modelRow, 2).toString());
                emailClienteField.setText(tableModel.getValueAt(modelRow, 3) != null ? tableModel.getValueAt(modelRow, 3).toString() : "");
                telefonoClienteField.setText(tableModel.getValueAt(modelRow, 4) != null ? tableModel.getValueAt(modelRow, 4).toString() : "");
            }
        });

        cargarClientes();
    }

    private void cargarClientes() {
        tableModel.setRowCount(0);
        // Usar SwingWorker para cargar datos en segundo plano
        new SwingWorker<List<Cliente>, Void>() {
            @Override
            protected List<Cliente> doInBackground() throws Exception {
                return clienteDAO.obtenerTodos();
            }

            @Override
            protected void done() {
                try {
                    List<Cliente> clientes = get();
                    for (Cliente cliente : clientes) {
                        tableModel.addRow(new Object[]{
                            cliente.getId(),
                            cliente.getNombre(),
                            cliente.getEmpresa(),
                            cliente.getEmail(),
                            cliente.getTelefono(),
                            cliente.getEstado()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelClientes.this, "Error al cargar clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void agregarCliente() {
        if (nombreClienteField.getText().isEmpty() || empresaClienteField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y empresa son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Cliente nuevoCliente = new Cliente(0, 
                nombreClienteField.getText(), 
                empresaClienteField.getText(), 
                emailClienteField.getText(), 
                telefonoClienteField.getText(),
                (String) estadoClienteComboBox.getSelectedItem(),
                notasClienteArea.getText());
            clienteDAO.agregarCliente(nuevoCliente);
            JOptionPane.showMessageDialog(this, "Cliente agregado con éxito.");
            limpiarCampos();
            cargarClientes();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCliente() {
        int selectedRow = clientesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int modelRow = clientesTable.convertRowIndexToModel(selectedRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            Cliente clienteActualizado = new Cliente(id, 
                nombreClienteField.getText(), 
                empresaClienteField.getText(), 
                emailClienteField.getText(), 
                telefonoClienteField.getText(),
                (String) estadoClienteComboBox.getSelectedItem(),
                notasClienteArea.getText());
            clienteDAO.actualizarCliente(clienteActualizado);
            JOptionPane.showMessageDialog(this, "Cliente actualizado con éxito.");
            limpiarCampos();
            cargarClientes();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarCliente() {
        int selectedRow = clientesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmación antes de eliminar
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este cliente?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int modelRow = clientesTable.convertRowIndexToModel(selectedRow);
                int id = (int) tableModel.getValueAt(modelRow, 0);
                clienteDAO.eliminarCliente(id);
                JOptionPane.showMessageDialog(this, "Cliente eliminado con éxito.");
                limpiarCampos();
                cargarClientes();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarCampos() {
        nombreClienteField.setText("");
        empresaClienteField.setText("");
        emailClienteField.setText("");
        telefonoClienteField.setText("");
        notasClienteArea.setText("");
        estadoClienteComboBox.setSelectedIndex(0);
        clientesTable.clearSelection();
    }
}