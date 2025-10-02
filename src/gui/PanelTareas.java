package gui;

import dao.ProyectoDAO;
import dao.TareaDAO;
import dao.EmpleadoDAO;
import modelo.Proyecto;
import modelo.Tarea;
import modelo.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class PanelTareas extends JPanel {
    private TareaDAO tareaDAO;
    private ProyectoDAO proyectoDAO;
    private EmpleadoDAO empleadoDAO;
    private JTable tablaTareas;
    private DefaultTableModel modeloTabla;
    private JComboBox<ProyectoItem> comboProyectos;
    private JComboBox<EmpleadoItem> comboEmpleados;
    private JTextArea txtDescripcion;
    private JDateChooser dateChooser;
    private JComboBox<String> comboEstado;

    public PanelTareas(TareaDAO tareaDAO, ProyectoDAO proyectoDAO, EmpleadoDAO empleadoDAO) {
        this.tareaDAO = tareaDAO;
        this.proyectoDAO = proyectoDAO;
        this.empleadoDAO = empleadoDAO;
        setLayout(new BorderLayout());
        inicializarComponentes();
        cargarDatosIniciales();
    }

    private void inicializarComponentes() {
        // Formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Gestionar Tarea"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panelFormulario.add(new JLabel("Proyecto:"), gbc);
        gbc.gridx = 1; comboProyectos = new JComboBox<>(); panelFormulario.add(comboProyectos, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelFormulario.add(new JLabel("Empleado Asignado:"), gbc);
        gbc.gridx = 1; comboEmpleados = new JComboBox<>(); panelFormulario.add(comboEmpleados, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panelFormulario.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.gridheight = 2;
        txtDescripcion = new JTextArea(3, 20);
        panelFormulario.add(new JScrollPane(txtDescripcion), gbc);
        gbc.gridheight = 1;

        gbc.gridx = 0; gbc.gridy = 4; panelFormulario.add(new JLabel("Fecha Límite:"), gbc);
        gbc.gridx = 1; dateChooser = new JDateChooser(); panelFormulario.add(dateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 5; panelFormulario.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; comboEstado = new JComboBox<>(new String[]{"Pendiente", "En progreso", "Completada"}); panelFormulario.add(comboEstado, gbc);

        add(panelFormulario, BorderLayout.NORTH);

        // Botones del formulario
        JPanel panelBotonesFormulario = new JPanel();
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarTarea());
        panelBotonesFormulario.add(btnAgregar);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizarTarea());
        panelBotonesFormulario.add(btnActualizar);
        
        gbc.gridx = 1; gbc.gridy = 6; panelFormulario.add(panelBotonesFormulario, gbc);


        // Tabla
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Proyecto", "Empleado", "Descripción", "Fecha Límite", "Estado"}, 0);
        tablaTareas = new JTable(modeloTabla);
        add(new JScrollPane(tablaTareas), BorderLayout.CENTER);
        
        tablaTareas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaTareas.getSelectedRow() != -1) {
                cargarTareaSeleccionada();
            }
        });

        // Botones de tabla
        JPanel panelBotonesTabla = new JPanel();
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarTarea());
        panelBotonesTabla.add(btnEliminar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarCampos());
        panelBotonesTabla.add(btnLimpiar);

        add(panelBotonesTabla, BorderLayout.SOUTH);
    }

    public void cargarDatosIniciales() {
        cargarTareasEnTabla();
        refrescarComboBoxes();
    }

    private void cargarTareasEnTabla() {
        modeloTabla.setRowCount(0);
        // CORRECCIÓN: Se agrega el bloque try-catch
        try {
            List<Tarea> tareas = tareaDAO.obtenerTodos();
            for (Tarea tarea : tareas) {
                modeloTabla.addRow(new Object[]{
                    tarea.getId(),
                    tarea.getNombreProyecto(),
                    tarea.getNombreEmpleado(),
                    tarea.getDescripcion(),
                    tarea.getFechaLimite(),
                    tarea.getEstado()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar las tareas.", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refrescarComboBoxes() {
        try {
            comboProyectos.removeAllItems();
            List<Proyecto> proyectos = proyectoDAO.obtenerTodos();
            for (Proyecto p : proyectos) {
                comboProyectos.addItem(new ProyectoItem(p.getId(), p.getNombre()));
            }

            comboEmpleados.removeAllItems();
            List<Empleado> empleados = empleadoDAO.obtenerTodos();
            for (Empleado e : empleados) {
                comboEmpleados.addItem(new EmpleadoItem(e.getId(), e.getNombre()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al refrescar listas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Resto de los métodos (agregarTarea, actualizarTarea, etc.) sin cambios...
    private void agregarTarea() {
        try {
            ProyectoItem proyectoItem = (ProyectoItem) comboProyectos.getSelectedItem();
            EmpleadoItem empleadoItem = (EmpleadoItem) comboEmpleados.getSelectedItem();
            String descripcion = txtDescripcion.getText();
            Date fechaLimite = dateChooser.getDate();
            String estado = (String) comboEstado.getSelectedItem();

            if (proyectoItem == null || empleadoItem == null || descripcion.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Proyecto, empleado y descripción son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Tarea nuevaTarea = new Tarea(0, proyectoItem.getId(), empleadoItem.getId(), descripcion, fechaLimite, estado);
            tareaDAO.agregarTarea(nuevaTarea);
            cargarTareasEnTabla();
            limpiarCampos();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al agregar la tarea.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTarea() {
        int filaSeleccionada = tablaTareas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una tarea para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idTarea = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
            ProyectoItem proyectoItem = (ProyectoItem) comboProyectos.getSelectedItem();
            EmpleadoItem empleadoItem = (EmpleadoItem) comboEmpleados.getSelectedItem();
            String descripcion = txtDescripcion.getText();
            Date fechaLimite = dateChooser.getDate();
            String estado = (String) comboEstado.getSelectedItem();

            Tarea tareaActualizada = new Tarea(idTarea, proyectoItem.getId(), empleadoItem.getId(), descripcion, fechaLimite, estado);
            tareaDAO.actualizarTarea(tareaActualizada);
            cargarTareasEnTabla();
            limpiarCampos();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar la tarea.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarTarea() {
         int filaSeleccionada = tablaTareas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una tarea para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idTarea = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                tareaDAO.eliminarTarea(idTarea);
                cargarTareasEnTabla();
                limpiarCampos();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar la tarea.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos() {
        tablaTareas.clearSelection();
        comboProyectos.setSelectedIndex(-1);
        comboEmpleados.setSelectedIndex(-1);
        txtDescripcion.setText("");
        dateChooser.setDate(null);
        comboEstado.setSelectedIndex(0);
    }
    
    private void cargarTareaSeleccionada() {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) return;

        String nombreProyecto = (String) modeloTabla.getValueAt(fila, 1);
        String nombreEmpleado = (String) modeloTabla.getValueAt(fila, 2);
        
        txtDescripcion.setText((String) modeloTabla.getValueAt(fila, 3));
        dateChooser.setDate((Date) modeloTabla.getValueAt(fila, 4));
        comboEstado.setSelectedItem(modeloTabla.getValueAt(fila, 5));
        
        for (int i = 0; i < comboProyectos.getItemCount(); i++) {
            if (comboProyectos.getItemAt(i).toString().equals(nombreProyecto)) {
                comboProyectos.setSelectedIndex(i);
                break;
            }
        }
        
        for (int i = 0; i < comboEmpleados.getItemCount(); i++) {
            if (comboEmpleados.getItemAt(i).toString().equals(nombreEmpleado)) {
                comboEmpleados.setSelectedIndex(i);
                break;
            }
        }
    }
}
