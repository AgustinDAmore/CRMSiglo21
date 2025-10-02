package gui;

import dao.ClienteDAO;
import dao.ProyectoDAO;
import modelo.Cliente;
import modelo.Proyecto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class PanelProyectos extends JPanel {

    private ProyectoDAO proyectoDAO;
    private ClienteDAO clienteDAO;
    private JTable proyectosTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField nombreProyectoField, filtroField;
    private JComboBox<ClienteItem> clienteComboBox;
    private JTextArea descripcionArea;
    private JComboBox<String> estadoProyectoComboBox;

    public PanelProyectos(ProyectoDAO proyectoDAO, ClienteDAO clienteDAO) {
        this.proyectoDAO = proyectoDAO;
        this.clienteDAO = clienteDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar Proyecto:"));
        filtroField = new JTextField(25);
        searchPanel.add(filtroField);

        // Tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Cliente ID", "Estado"}, 0);
        proyectosTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        proyectosTable.setRowSorter(sorter);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(proyectosTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel proyectosInputPanel = new JPanel(new GridBagLayout());
        proyectosInputPanel.setBorder(BorderFactory.createTitledBorder("Datos del Proyecto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nombreProyectoField = new JTextField(20);
        clienteComboBox = new JComboBox<>();
        descripcionArea = new JTextArea(4, 20);
        estadoProyectoComboBox = new JComboBox<>(new String[]{"En curso", "Finalizado", "Cancelado"});
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        proyectosInputPanel.add(new JLabel("Nombre Proyecto:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1;
        proyectosInputPanel.add(nombreProyectoField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        proyectosInputPanel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1;
        proyectosInputPanel.add(clienteComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        proyectosInputPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1;
        proyectosInputPanel.add(estadoProyectoComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHEAST; gbc.weightx = 0;
        proyectosInputPanel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
        proyectosInputPanel.add(new JScrollPane(descripcionArea), gbc);

        JPanel proyectosButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addProyectoBtn = new JButton("Agregar Proyecto");
        JButton updateProyectoBtn = new JButton("Actualizar Proyecto");
        JButton deleteProyectoBtn = new JButton("Eliminar Proyecto");
        proyectosButtonPanel.add(addProyectoBtn);
        proyectosButtonPanel.add(updateProyectoBtn);
        proyectosButtonPanel.add(deleteProyectoBtn);

        add(proyectosInputPanel, BorderLayout.NORTH);
        add(proyectosButtonPanel, BorderLayout.SOUTH);
        
        // Listeners
        addProyectoBtn.addActionListener(e -> agregarProyecto());
        updateProyectoBtn.addActionListener(e -> actualizarProyecto());
        deleteProyectoBtn.addActionListener(e -> eliminarProyecto());
        
        filtroField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = filtroField.getText();
                sorter.setRowFilter(text.trim().length() == 0 ? null : RowFilter.regexFilter("(?i)" + text));
            }
        });
        
        refrescarClientesComboBox();
        cargarProyectos();
    }
    
    public void refrescarClientesComboBox() {
        new SwingWorker<Vector<ClienteItem>, Void>() {
            @Override
            protected Vector<ClienteItem> doInBackground() throws Exception {
                List<Cliente> clientes = clienteDAO.obtenerTodos();
                Vector<ClienteItem> items = new Vector<>();
                for (Cliente cliente : clientes) {
                    items.add(new ClienteItem(cliente.getId(), cliente.getNombre()));
                }
                return items;
            }

            @Override
            protected void done() {
                try {
                    clienteComboBox.setModel(new DefaultComboBoxModel<>(get()));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelProyectos.this, "Error al cargar clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void cargarProyectos() {
        tableModel.setRowCount(0);
        new SwingWorker<List<Proyecto>, Void>() {
            @Override
            protected List<Proyecto> doInBackground() throws Exception {
                return proyectoDAO.obtenerTodos();
            }

            @Override
            protected void done() {
                try {
                    for (Proyecto proyecto : get()) {
                        tableModel.addRow(new Object[]{
                            proyecto.getId(),
                            proyecto.getNombre(),
                            proyecto.getIdCliente(),
                            proyecto.getEstado()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelProyectos.this, "Error al cargar proyectos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void agregarProyecto() {
        ClienteItem clienteSeleccionado = (ClienteItem) clienteComboBox.getSelectedItem();
        if (clienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Proyecto nuevoProyecto = new Proyecto(0, 
                nombreProyectoField.getText(), 
                descripcionArea.getText(), 
                clienteSeleccionado.getId(), 
                (String) estadoProyectoComboBox.getSelectedItem(), 
                new java.sql.Date(System.currentTimeMillis()), 
                null);
            proyectoDAO.agregarProyecto(nuevoProyecto);
            JOptionPane.showMessageDialog(this, "Proyecto agregado con éxito.");
            cargarProyectos();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar proyecto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarProyecto() {
        int filaSeleccionada = proyectosTable.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un proyecto para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ClienteItem clienteSeleccionado = (ClienteItem) clienteComboBox.getSelectedItem();
        if (clienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int modelRow = proyectosTable.convertRowIndexToModel(filaSeleccionada);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            Proyecto proyectoActualizado = new Proyecto(id, 
                nombreProyectoField.getText(), 
                descripcionArea.getText(), 
                clienteSeleccionado.getId(), 
                (String) estadoProyectoComboBox.getSelectedItem(), 
                null, 
                null); 
            proyectoDAO.actualizarProyecto(proyectoActualizado);
            JOptionPane.showMessageDialog(this, "Proyecto actualizado con éxito.");
            cargarProyectos();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar proyecto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProyecto() {
        int filaSeleccionada = proyectosTable.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un proyecto para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de que desea eliminar este proyecto?\n¡Esto eliminará también todas sus tareas, gastos y facturas asociadas!", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int modelRow = proyectosTable.convertRowIndexToModel(filaSeleccionada);
                int id = (int) tableModel.getValueAt(modelRow, 0);
                proyectoDAO.eliminarProyecto(id);
                JOptionPane.showMessageDialog(this, "Proyecto eliminado con éxito.");
                cargarProyectos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar proyecto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}