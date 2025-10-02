package gui;

import com.toedter.calendar.JDateChooser;
import dao.EmpleadoDAO;
import dao.RegistroHoraDAO;
import dao.TareaDAO;
import modelo.Empleado;
import modelo.RegistroHora;
import modelo.Tarea;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class PanelRegistroHoras extends JPanel {

    private RegistroHoraDAO registroHoraDAO;
    private TareaDAO tareaDAO;
    private EmpleadoDAO empleadoDAO;

    private JTable registrosTable;
    private DefaultTableModel tableModel;
    private JComboBox<TareaItem> tareaComboBox;
    private JComboBox<EmpleadoItem> empleadoComboBox;
    private JTextField horasField;
    private JDateChooser fechaChooser;
    private JTextArea descripcionArea;

    public PanelRegistroHoras(RegistroHoraDAO registroHoraDAO, TareaDAO tareaDAO, EmpleadoDAO empleadoDAO) {
        this.registroHoraDAO = registroHoraDAO;
        this.tareaDAO = tareaDAO;
        this.empleadoDAO = empleadoDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de entrada de datos
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Registrar Horas Trabajadas"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tareaComboBox = new JComboBox<>();
        empleadoComboBox = new JComboBox<>();
        horasField = new JTextField(5);
        fechaChooser = new JDateChooser();
        fechaChooser.setDateFormatString("yyyy-MM-dd");
        fechaChooser.setDate(new java.util.Date());
        descripcionArea = new JTextArea(3, 20);

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Tarea:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; inputPanel.add(tareaComboBox, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Empleado:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(empleadoComboBox, gbc);
        gbc.gridx = 2; gbc.gridy = 1; inputPanel.add(new JLabel("Horas:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; inputPanel.add(horasField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(fechaChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHEAST; inputPanel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(new JScrollPane(descripcionArea), gbc);

        JButton addBtn = new JButton("Registrar Horas");
        gbc.gridx = 3; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(addBtn, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Tabla para mostrar los registros
        tableModel = new DefaultTableModel(new String[]{"ID", "ID Tarea", "ID Empleado", "Horas", "Fecha", "Descripción"}, 0);
        registrosTable = new JTable(tableModel);
        add(new JScrollPane(registrosTable), BorderLayout.CENTER);

        // Panel de botones inferior
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteBtn = new JButton("Eliminar Registro Seleccionado");
        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        tareaComboBox.addActionListener(e -> cargarRegistrosDeTarea());
        addBtn.addActionListener(e -> agregarRegistro());
        deleteBtn.addActionListener(e -> eliminarRegistro());

        refrescarComboBoxes();
    }

    public void refrescarComboBoxes() {
        // Cargar Tareas
        new SwingWorker<Vector<TareaItem>, Void>() {
            @Override
            protected Vector<TareaItem> doInBackground() throws Exception {
                List<Tarea> tareas = tareaDAO.obtenerTodas();
                Vector<TareaItem> items = new Vector<>();
                for (Tarea tarea : tareas) {
                    items.add(new TareaItem(tarea.getId(), tarea.getDescripcion()));
                }
                return items;
            }

            @Override
            protected void done() {
                try {
                    tareaComboBox.setModel(new DefaultComboBoxModel<>(get()));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelRegistroHoras.this, "Error al cargar tareas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();

        // Cargar Empleados
        new SwingWorker<Vector<EmpleadoItem>, Void>() {
            @Override
            protected Vector<EmpleadoItem> doInBackground() throws Exception {
                List<Empleado> empleados = empleadoDAO.obtenerTodos();
                Vector<EmpleadoItem> items = new Vector<>();
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
                    JOptionPane.showMessageDialog(PanelRegistroHoras.this, "Error al cargar empleados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void cargarRegistrosDeTarea() {
        tableModel.setRowCount(0);
        TareaItem selected = (TareaItem) tareaComboBox.getSelectedItem();
        if (selected == null) return;

        new SwingWorker<List<RegistroHora>, Void>() {
            @Override
            protected List<RegistroHora> doInBackground() throws Exception {
                return registroHoraDAO.obtenerPorTarea(selected.getId());
            }

            @Override
            protected void done() {
                try {
                    for (RegistroHora registro : get()) {
                        tableModel.addRow(new Object[]{
                            registro.getId(),
                            registro.getIdTarea(),
                            registro.getIdEmpleado(),
                            registro.getHoras(),
                            registro.getFecha(),
                            registro.getDescripcion()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelRegistroHoras.this, "Error al cargar registros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void agregarRegistro() {
        TareaItem tareaSel = (TareaItem) tareaComboBox.getSelectedItem();
        EmpleadoItem empleadoSel = (EmpleadoItem) empleadoComboBox.getSelectedItem();

        if (tareaSel == null || empleadoSel == null || horasField.getText().trim().isEmpty() || fechaChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double horas = Double.parseDouble(horasField.getText());
            Date fecha = new Date(fechaChooser.getDate().getTime());

            RegistroHora nuevoRegistro = new RegistroHora(0, tareaSel.getId(), empleadoSel.getId(), horas, fecha, descripcionArea.getText());
            registroHoraDAO.agregarRegistro(nuevoRegistro);
            JOptionPane.showMessageDialog(this, "Registro agregado con éxito.");
            
            // Limpiar campos
            horasField.setText("");
            descripcionArea.setText("");
            
            cargarRegistrosDeTarea(); // Recargar tabla
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Las horas deben ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar el registro: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarRegistro() {
        int selectedRow = registrosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int idRegistro = (int) registrosTable.getValueAt(selectedRow, 0);
                registroHoraDAO.eliminarRegistro(idRegistro);
                cargarRegistrosDeTarea(); // Recargar la tabla
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el registro: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Clase interna para el ComboBox de Tareas
    private class TareaItem {
        private int id;
        private String descripcion;

        public TareaItem(int id, String descripcion) {
            this.id = id;
            this.descripcion = descripcion;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "ID " + id + ": " + (descripcion.length() > 50 ? descripcion.substring(0, 50) + "..." : descripcion);
        }
    }
}