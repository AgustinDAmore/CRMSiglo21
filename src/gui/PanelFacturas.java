package gui;

import dao.FacturaDAO;
import dao.ProyectoDAO;
import modelo.Factura;
import modelo.Proyecto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelFacturas extends JPanel {

    private FacturaDAO facturaDAO;
    private ProyectoDAO proyectoDAO;
    private JTable facturasTable;
    private DefaultTableModel tableModel;
    
    private JComboBox<ProyectoItem> proyectoComboBox; 
    private JTextField codigoFacturaField; // <-- CAMPO AÑADIDO
    private JTextField montoFacturaField;
    private JComboBox<String> estadoFacturaComboBox;

    public PanelFacturas(FacturaDAO facturaDAO, ProyectoDAO proyectoDAO) {
        this.facturaDAO = facturaDAO;
        this.proyectoDAO = proyectoDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Se añade la columna "Código" a la tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Código", "Proyecto ID", "Monto", "Fecha", "Estado"}, 0);
        facturasTable = new JTable(tableModel);
        add(new JScrollPane(facturasTable), BorderLayout.CENTER);

        JPanel facturasInputPanel = new JPanel(new GridBagLayout());
        facturasInputPanel.setBorder(BorderFactory.createTitledBorder("Datos de la Factura"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        proyectoComboBox = new JComboBox<>();
        codigoFacturaField = new JTextField(15); // <-- INICIALIZACIÓN
        montoFacturaField = new JTextField(15);
        estadoFacturaComboBox = new JComboBox<>(new String[]{"Pendiente", "Pagada", "Vencida"});

        // Fila 1: Proyecto
        gbc.gridx = 0; gbc.gridy = 0;
        facturasInputPanel.add(new JLabel("Proyecto:"), gbc);
        gbc.gridx = 1;
        facturasInputPanel.add(proyectoComboBox, gbc);

        // Fila 2: Código de Factura (NUEVA FILA)
        gbc.gridx = 0; gbc.gridy = 1;
        facturasInputPanel.add(new JLabel("Código Factura:"), gbc);
        gbc.gridx = 1;
        facturasInputPanel.add(codigoFacturaField, gbc);

        // Fila 3: Monto
        gbc.gridx = 0; gbc.gridy = 2;
        facturasInputPanel.add(new JLabel("Monto:"), gbc);
        gbc.gridx = 1;
        facturasInputPanel.add(montoFacturaField, gbc);
        
        // Fila 4: Estado
        gbc.gridx = 0; gbc.gridy = 3;
        facturasInputPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        facturasInputPanel.add(estadoFacturaComboBox, gbc);

        JPanel facturasButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addFacturaBtn = new JButton("Generar Factura");
        JButton deleteFacturaBtn = new JButton("Eliminar Factura");
        facturasButtonPanel.add(addFacturaBtn);
        facturasButtonPanel.add(deleteFacturaBtn);

        add(facturasInputPanel, BorderLayout.NORTH);
        add(facturasButtonPanel, BorderLayout.SOUTH);

        addFacturaBtn.addActionListener(e -> agregarFactura());
        deleteFacturaBtn.addActionListener(e -> eliminarFactura());
        
        refrescarProyectosComboBox();
        cargarFacturas();
    }

    public void refrescarProyectosComboBox() {
        try {
            List<Proyecto> proyectos = proyectoDAO.obtenerTodos();
            proyectoComboBox.removeAllItems();
            for (Proyecto proyecto : proyectos) {
                proyectoComboBox.addItem(new ProyectoItem(proyecto.getId(), proyecto.getNombre()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proyectos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarFacturas() {
        tableModel.setRowCount(0);
        try {
            List<Factura> facturas = facturaDAO.obtenerTodos();
            for (Factura factura : facturas) {
                // Se añade el código a la fila de la tabla
                tableModel.addRow(new Object[]{
                    factura.getId(),
                    factura.getCodigo(), // <-- CAMPO AÑADIDO
                    factura.getIdProyecto(),
                    factura.getMonto(),
                    factura.getFechaEmision(),
                    factura.getEstado()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar facturas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarFactura() {
        ProyectoItem itemSeleccionado = (ProyectoItem) proyectoComboBox.getSelectedItem();
        if (itemSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proyecto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String codigo = codigoFacturaField.getText();
        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El código de la factura es obligatorio.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Factura nuevaFactura = new Factura(0, 
                itemSeleccionado.getId(),
                codigo, // <-- CAMPO AÑADIDO
                Double.parseDouble(montoFacturaField.getText()), 
                null,
                (String) estadoFacturaComboBox.getSelectedItem());

            facturaDAO.agregarFactura(nuevaFactura);
            JOptionPane.showMessageDialog(this, "Factura generada con éxito.");
            cargarFacturas();
            codigoFacturaField.setText("");
            montoFacturaField.setText("");
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, "Error: El código de factura '" + codigo + "' ya existe.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al generar factura: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en el formato del monto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarFactura() {
        int filaSeleccionada = facturasTable.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una factura para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) facturasTable.getValueAt(filaSeleccionada, 0);
            facturaDAO.eliminarFactura(id);
            JOptionPane.showMessageDialog(this, "Factura eliminada con éxito.");
            cargarFacturas();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar factura: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}