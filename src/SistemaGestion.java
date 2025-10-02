import dao.*;
import gui.*;
import modelo.Usuario;
import util.ConfiguracionDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class SistemaGestion extends JFrame {

    private Connection conexion;
    private UsuarioDAO usuarioDAO;
    private ClienteDAO clienteDAO;
    private ProyectoDAO proyectoDAO;
    private FacturaDAO facturaDAO;
    private TareaDAO tareaDAO;
    private EmpleadoDAO empleadoDAO;
    private GastoDAO gastoDAO;
    private RegistroHoraDAO registroHoraDAO;
    private JTabbedPane tabbedPane;
    private Usuario usuarioLogueado;

    // Referencias a los paneles
    private PanelProyectos panelProyectos;
    private PanelTareas panelTareas;
    private PanelFacturas panelFacturas;
    private PanelGastos panelGastos;
    private PanelRegistroHoras panelRegistroHoras;
    private PanelUsuarios panelUsuarios;

    public SistemaGestion() {
        super("Sistema de Gestión de Microempresa v5.1"); // Versión actualizada

        if (!conectarBD()) {
            System.exit(1); // Salir si no hay conexión
        }

        inicializarDAOs();

        // El login ahora se maneja dentro de inicializarUI
        if (inicializarUI()) {
            mostrarNotificacionesIniciales();
            setVisible(true); // Mostrar la ventana principal solo si el login es exitoso
        } else {
            // Si el login se cancela o falla, se cierra la aplicación
            System.exit(0);
        }
    }

    private boolean conectarBD() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_empresa", "root", "");
            ConfiguracionDB.crearTablasSiNoExisten(conexion);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error crítico de conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void inicializarDAOs() {
        usuarioDAO = new UsuarioDAO(conexion);
        clienteDAO = new ClienteDAO(conexion);
        proyectoDAO = new ProyectoDAO(conexion);
        facturaDAO = new FacturaDAO(conexion);
        tareaDAO = new TareaDAO(conexion);
        empleadoDAO = new EmpleadoDAO(conexion);
        gastoDAO = new GastoDAO(conexion);
        registroHoraDAO = new RegistroHoraDAO(conexion);
    }

    private boolean inicializarUI() {
        VentanaLogin loginDialog = new VentanaLogin(this, usuarioDAO);
        loginDialog.setVisible(true);
        usuarioLogueado = loginDialog.getUsuarioAutenticado();

        if (usuarioLogueado == null) {
            return false; // Login fallido o cancelado
        }

        setTitle("Sistema de Gestión");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // --- INICIO DE MODIFICACIÓN PARA CERRAR SESIÓN ---

        // 1. Panel principal que contendrá todo
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // 2. Panel superior para información de usuario y botón de cerrar sesión
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel userInfoLabel = new JLabel("Usuario: " + usuarioLogueado.getNombreUsuario() + " (" + usuarioLogueado.getRol() + ")");
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JButton logoutButton = new JButton("Cerrar Sesión");
        
        topPanel.add(userInfoLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- FIN DE MODIFICACIÓN ---

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        PanelDashboard panelDashboard = new PanelDashboard(proyectoDAO, facturaDAO, tareaDAO);
        PanelClientes panelClientes = new PanelClientes(clienteDAO);
        panelProyectos = new PanelProyectos(proyectoDAO, clienteDAO);
        panelFacturas = new PanelFacturas(facturaDAO, proyectoDAO);
        panelTareas = new PanelTareas(tareaDAO, proyectoDAO, empleadoDAO);
        PanelEmpleados panelEmpleados = new PanelEmpleados(empleadoDAO);
        panelGastos = new PanelGastos(gastoDAO, proyectoDAO);
        panelRegistroHoras = new PanelRegistroHoras(registroHoraDAO, tareaDAO, empleadoDAO);
        panelUsuarios = new PanelUsuarios(usuarioDAO, empleadoDAO);

        tabbedPane.addTab("Dashboard", panelDashboard);
        if (usuarioLogueado.getRol().equals("Administrador") || usuarioLogueado.getRol().equals("Gestor")) {
            tabbedPane.addTab("Clientes", panelClientes);
            tabbedPane.addTab("Proyectos", panelProyectos);
            tabbedPane.addTab("Facturas", panelFacturas);
            tabbedPane.addTab("Gastos", panelGastos);
        }
        tabbedPane.addTab("Tareas", panelTareas);
        tabbedPane.addTab("Registro de Horas", panelRegistroHoras);
        if (usuarioLogueado.getRol().equals("Administrador")) {
            tabbedPane.addTab("Empleados", panelEmpleados);
            tabbedPane.addTab("Gestión de Usuarios", panelUsuarios);
        }
        
        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected == panelProyectos) panelProyectos.refrescarClientesComboBox();
            else if (selected == panelTareas) panelTareas.refrescarComboBoxes();
            else if (selected == panelFacturas) panelFacturas.refrescarProyectosComboBox();
            else if (selected == panelGastos) panelGastos.refrescarProyectosComboBox();
            else if (selected == panelRegistroHoras) panelRegistroHoras.refrescarComboBoxes();
            else if (selected == panelUsuarios) panelUsuarios.refrescarEmpleadosComboBox();
        });
        
        // Se añade el tabbedPane al panel principal
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Se establece el panel principal como el contenido del frame
        setContentPane(mainPanel);

        // Acción del botón de cerrar sesión
        logoutButton.addActionListener(e -> cerrarSesion());
        
        return true; // Login exitoso
    }
    
    private void cerrarSesion() {
        dispose(); // Cierra la ventana actual
        // Vuelve a lanzar la aplicación desde el punto de entrada
        SwingUtilities.invokeLater(() -> main(null));
    }
    
    private void mostrarNotificacionesIniciales() {
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return tareaDAO.contarTareasPorVencer();
            }

            @Override
            protected void done() {
                try {
                    int tareasPorVencer = get();
                    if (tareasPorVencer > 0) {
                        JOptionPane.showMessageDialog(SistemaGestion.this,
                            "Atención: Tiene " + tareasPorVencer + " tarea(s) que vencen en los próximos 7 días.",
                            "Notificación de Tareas",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (InterruptedException | ExecutionException e) {
                     JOptionPane.showMessageDialog(SistemaGestion.this,
                        "No se pudieron cargar las notificaciones de tareas.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        // Se crea una nueva instancia de la aplicación cada vez que se llama a main
        new SistemaGestion();
    }
}