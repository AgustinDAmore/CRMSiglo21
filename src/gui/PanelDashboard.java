package gui;

import dao.FacturaDAO;
import dao.ProyectoDAO;
import dao.TareaDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class PanelDashboard extends JPanel {

    private ProyectoDAO proyectoDAO;
    private FacturaDAO facturaDAO;
    private TareaDAO tareaDAO;

    private JLabel proyectosActivosLabel;
    private JLabel facturasPendientesLabel;
    private JLabel tareasPorVencerLabel;
    private JLabel ingresosMesLabel;

    public PanelDashboard(ProyectoDAO proyectoDAO, FacturaDAO facturaDAO, TareaDAO tareaDAO) {
        this.proyectoDAO = proyectoDAO;
        this.facturaDAO = facturaDAO;
        this.tareaDAO = tareaDAO;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titulo = new JLabel("Dashboard Principal", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        add(titulo, BorderLayout.NORTH);

        JPanel metricasPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        proyectosActivosLabel = new JLabel("0", SwingConstants.CENTER);
        facturasPendientesLabel = new JLabel("0", SwingConstants.CENTER);
        tareasPorVencerLabel = new JLabel("0", SwingConstants.CENTER);
        ingresosMesLabel = new JLabel("$0.00", SwingConstants.CENTER);

        metricasPanel.add(crearMetrica("Proyectos Activos", proyectosActivosLabel));
        metricasPanel.add(crearMetrica("Facturas Pendientes", facturasPendientesLabel));
        metricasPanel.add(crearMetrica("Tareas Próximas a Vencer", tareasPorVencerLabel));
        metricasPanel.add(crearMetrica("Ingresos del Mes (Pagado)", ingresosMesLabel));
        
        add(metricasPanel, BorderLayout.CENTER);
        
        actualizarMetricas();
    }

    private void actualizarMetricas() {
        try {
            proyectosActivosLabel.setText(String.valueOf(proyectoDAO.contarProyectosActivos()));
            facturasPendientesLabel.setText(String.valueOf(facturaDAO.contarFacturasPendientes()));
            tareasPorVencerLabel.setText(String.valueOf(tareaDAO.contarTareasPorVencer()));
            ingresosMesLabel.setText(String.format("$%.2f", facturaDAO.sumarIngresosDelMes()));
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar las métricas del dashboard.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearMetrica(String titulo, JLabel valorLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        JLabel tituloLabel = new JLabel(titulo, SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        valorLabel.setFont(new Font("Arial", Font.PLAIN, 28));
        
        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(valorLabel, BorderLayout.CENTER);
        return panel;
    }
}