package gui;

import dao.EmpleadoDAO;
import dao.RegistroHoraDAO;
import dao.TareaDAO;
import modelo.Empleado;
import modelo.RegistroHora;
import modelo.Tarea;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class PanelRegistroHoras extends JPanel {
    private RegistroHoraDAO registroHoraDAO;
    private TareaDAO tareaDAO;
    private EmpleadoDAO empleadoDAO;

    private JComboBox<TareaItem> comboTareas;
    private JComboBox<EmpleadoItem> comboEmpleados;
    private JSpinner spinnerHoras;
    private JDateChooser dateChooser;
    private JTextArea txtDescripcion;

    public PanelRegistroHoras(RegistroHoraDAO registroHoraDAO, TareaDAO tareaDAO, EmpleadoDAO empleadoDAO) {
        this.registroHoraDAO = registroHoraDAO;
        this.tareaDAO = tareaDAO;
        this.empleadoDAO = empleadoDAO;
        setLayout(new BorderLayout());
        inicializarComponentes();
        refrescarComboBoxes();
    }

    private void inicializarComponentes() {
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Registrar Horas"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panelFormulario.add(new JLabel("Tarea:"), gbc);
        gbc.gridx = 1; comboTareas = new JComboBox<>(); panelFormulario.add(comboTareas, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelFormulario.add(new JLabel("Empleado:"), gbc);
        gbc.gridx = 1; comboEmpleados = new JComboBox<>(); panelFormulario.add(comboEmpleados, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; panelFormulario.add(new JLabel("Horas:"), gbc);
        gbc.gridx = 1; spinnerHoras = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 24.0, 0.5)); panelFormulario.add(spinnerHoras, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panelFormulario.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; dateChooser = new JDateChooser(new Date()); panelFormulario.add(dateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panelFormulario.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.gridheight = 2;
        txtDescripcion = new JTextArea(3, 20);
        panelFormulario.add(new JScrollPane(txtDescripcion), gbc);
        gbc.gridheight = 1;

        JButton btnRegistrar = new JButton("Registrar Horas");
        btnRegistrar.addActionListener(e -> registrarHoras());
        gbc.gridx = 1; gbc.gridy = 6; gbc.anchor = GridBagConstraints.EAST;
        panelFormulario.add(btnRegistrar, gbc);

        add(panelFormulario, BorderLayout.NORTH);
    }

    public void refrescarComboBoxes() {
        try {
            comboTareas.removeAllItems();
            // CORRECCIÓN: Se agrega el bloque try-catch
            List<Tarea> tareas = tareaDAO.obtenerTodos();
            for (Tarea t : tareas) {
                comboTareas.addItem(new TareaItem(t.getId(), t.getDescripcion()));
            }

            comboEmpleados.removeAllItems();
            List<Empleado> empleados = empleadoDAO.obtenerTodos();
            for (Empleado e : empleados) {
                comboEmpleados.addItem(new EmpleadoItem(e.getId(), e.getNombre()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar listas desplegables.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarHoras() {
        try {
            TareaItem tareaItem = (TareaItem) comboTareas.getSelectedItem();
            EmpleadoItem empleadoItem = (EmpleadoItem) comboEmpleados.getSelectedItem();
            double horas = (Double) spinnerHoras.getValue();
            Date fecha = dateChooser.getDate();
            String descripcion = txtDescripcion.getText();

            if (tareaItem == null || empleadoItem == null || fecha == null) {
                JOptionPane.showMessageDialog(this, "Tarea, empleado y fecha son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RegistroHora nuevoRegistro = new RegistroHora(0, tareaItem.getId(), empleadoItem.getId(), horas, fecha, descripcion);
            registroHoraDAO.agregarRegistro(nuevoRegistro);
            JOptionPane.showMessageDialog(this, "Horas registradas exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al registrar las horas.", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarCampos() {
        comboTareas.setSelectedIndex(-1);
        comboEmpleados.setSelectedIndex(-1);
        spinnerHoras.setValue(1.0);
        dateChooser.setDate(new Date());
        txtDescripcion.setText("");
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
            return descripcion;
        }
    }
}
