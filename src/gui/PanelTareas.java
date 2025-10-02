package gui;

import com.toedter.calendar.JDateChooser;
import dao.EmpleadoDAO;
import dao.ProyectoDAO;
import dao.TareaDAO;
import modelo.Empleado;
import modelo.Proyecto;
import modelo.Tarea;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class PanelTareas extends JPanel {

    private TareaDAO tareaDAO;
    private ProyectoDAO proyectoDAO;
    private EmpleadoDAO empleadoDAO;

    private JTable tareasTable;
    private DefaultTableModel tableModel;
    
    private JComboBox<ProyectoItem> proyectoComboBox;
    private JComboBox<EmpleadoItem> empleadoComboBox;
    private JTextField descripcionField;
    private JDateChooser fechaLimiteChooser;
    private JComboBox<String> estadoComboBox;

    public PanelTareas(TareaDAO tareaDAO, ProyectoDAO proyectoDAO, EmpleadoDAO empleadoDAO) {
        this.tareaDAO = tareaDAO;
        this.proyectoDAO = proyectoDAO;
        this.empleadoDAO = empleadoDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(new String[]{"ID", "Proyecto ID", "Empleado ID", "Descripción", "Fecha Límite", "Estado"}, 0);
        tareasTable = new JTable(tableModel);
        add(new JScrollPane(tareasTable), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Datos de la Tarea"));
        
        proyectoComboBox = new JComboBox<>();
        empleadoComboBox = new JComboBox<>();
        descripcionField = new JTextField();
        
        fechaLimiteChooser = new JDateChooser();
        fechaLimiteChooser.setDateFormatString("yyyy-MM-dd");
        fechaLimiteChooser.setMinSelectableDate(new java.util.Date()); 
        
        estadoComboBox = new JComboBox<>(new String[]{"Pendiente", "En progreso", "Completada"});

        inputPanel.add(new JLabel("Proyecto:"));
        inputPanel.add(proyectoComboBox);
        inputPanel.add(new JLabel("Asignar a Empleado:"));
        inputPanel.add(empleadoComboBox);
        inputPanel.add(new JLabel("Descripción:"));
        inputPanel.add(descripcionField);
        inputPanel.add(new JLabel("Fecha Límite:"));
        inputPanel.add(fechaLimiteChooser);
        inputPanel.add(new JLabel("Estado:"));
        inputPanel.add(estadoComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn = new JButton("Agregar Tarea");
        JButton updateBtn = new JButton("Actualizar Tarea");
        JButton deleteBtn = new JButton("Eliminar Tarea");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> agregarTarea());
        updateBtn.addActionListener(e -> actualizarTarea());
        deleteBtn.addActionListener(e -> eliminarTarea());

        refrescarComboBoxes();
        cargarTareas();
    }
    
    public void refrescarComboBoxes() {
        // Cargar Proyectos
        new SwingWorker<Vector<ProyectoItem>, Void>() {
            @Override
            protected Vector<ProyectoItem> doInBackground() throws Exception {
                List<Proyecto> proyectos = proyectoDAO.obtenerTodos();
                Vector<ProyectoItem> items = new Vector<>();
                for (Proyecto proyecto : proyectos) {
                    items.add(new ProyectoItem(proyecto.getId(), proyecto.getNombre()));
                }
                return items;
            }

            @Override
            protected void done() {
                try {
                    proyectoComboBox.setModel(new DefaultComboBoxModel<>(get()));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelTareas.this, "Error al cargar proyectos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(PanelTareas.this, "Error al cargar empleados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    private void cargarTareas() {
        tableModel.setRowCount(0);
        new SwingWorker<List<Tarea>, Void>() {
            @Override
            protected List<Tarea> doInBackground() throws Exception {
                return tareaDAO.obtenerTodas();
            }

            @Override
            protected void done() {
                try {
                    for (Tarea tarea : get()) {
                        tableModel.addRow(new Object[]{
                            tarea.getId(),
                            tarea.getIdProyecto(),
                            tarea.getIdEmpleado(),
                            tarea.getDescripcion(),
                            tarea.getFechaLimite(),
                            tarea.getEstado()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelTareas.this, "Error al cargar tareas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void agregarTarea() {
        if (!validarCampos()) return;
        
        ProyectoItem proyectoSel = (ProyectoItem) proyectoComboBox.getSelectedItem();
        EmpleadoItem empleadoSel = (EmpleadoItem) empleadoComboBox.getSelectedItem();
        if (proyectoSel == null || empleadoSel == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proyecto y un empleado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            java.util.Date utilDate = fechaLimiteChooser.getDate();
            Date sqlDate = new Date(utilDate.getTime());

            Tarea nuevaTarea = new Tarea(0,
                proyectoSel.getId(),
                empleadoSel.getId(),
                descripcionField.getText(),
                sqlDate,
                (String) estadoComboBox.getSelectedItem());
            tareaDAO.agregarTarea(nuevaTarea);
            JOptionPane.showMessageDialog(this, "Tarea agregada con éxito.");
            limpiarCampos();
            cargarTareas();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar tarea: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTarea() {
        int filaSeleccionada = tareasTable.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;
        
        ProyectoItem proyectoSel = (ProyectoItem) proyectoComboBox.getSelectedItem();
        EmpleadoItem empleadoSel = (EmpleadoItem) empleadoComboBox.getSelectedItem();
        if (proyectoSel == null || empleadoSel == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proyecto y un empleado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            java.util.Date utilDate = fechaLimiteChooser.getDate();
            Date sqlDate = new Date(utilDate.getTime());

            int id = (int) tareasTable.getValueAt(filaSeleccionada, 0);
            Tarea tareaActualizada = new Tarea(id,
                proyectoSel.getId(),
                empleadoSel.getId(),
                descripcionField.getText(),
                sqlDate,
                (String) estadoComboBox.getSelectedItem());
            tareaDAO.actualizarTarea(tareaActualizada);
            JOptionPane.showMessageDialog(this, "Tarea actualizada con éxito.");
            limpiarCampos();
            cargarTareas();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar tarea: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarCampos() {
        if (descripcionField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La descripción es obligatoria.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (fechaLimiteChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La fecha límite es obligatoria.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void eliminarTarea() {
        int filaSeleccionada = tareasTable.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar esta tarea?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tareasTable.getValueAt(filaSeleccionada, 0);
                tareaDAO.eliminarTarea(id);
                JOptionPane.showMessageDialog(this, "Tarea eliminada con éxito.");
                limpiarCampos();
                cargarTareas();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar tarea: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos() {
        descripcionField.setText("");
        fechaLimiteChooser.setDate(null);
        proyectoComboBox.setSelectedIndex(-1);
        empleadoComboBox.setSelectedIndex(-1);
        estadoComboBox.setSelectedIndex(0);
        tareasTable.clearSelection();
    }
}