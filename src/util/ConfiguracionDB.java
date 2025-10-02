package util;

// --- IMPORTS QUE FALTABAN, AHORA SÍ ESTÁN ---
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de utilidad para manejar la configuración inicial de la base de datos.
 */
public class ConfiguracionDB {

    /**
     * Revisa si las tablas existen y las crea en caso de que no.
     * También inserta un usuario administrador por defecto si la tabla de usuarios está vacía.
     * @param conexion La conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error al ejecutar las sentencias SQL.
     */
    public static void crearTablasSiNoExisten(Connection conexion) throws SQLException {
        Statement stmt = conexion.createStatement();

        // --- TABLAS ORIGINALES ---
        String sqlClientes = "CREATE TABLE IF NOT EXISTS `clientes` ("
                + "`id_cliente` INT PRIMARY KEY AUTO_INCREMENT,"
                + "`nombre` VARCHAR(100) NOT NULL,"
                + "`empresa` VARCHAR(100) NOT NULL,"
                + "`email` VARCHAR(100),"
                + "`telefono` VARCHAR(20),"
                + "`estado` VARCHAR(20) DEFAULT 'Activo',"
                + "`notas` TEXT"
                + ")";
        stmt.executeUpdate(sqlClientes);

        String sqlProyectos = "CREATE TABLE IF NOT EXISTS `proyectos` ("
                + "`id_proyecto` INT PRIMARY KEY AUTO_INCREMENT,"
                + "`nombre_proyecto` VARCHAR(100) NOT NULL,"
                + "`descripcion` TEXT,"
                + "`id_cliente` INT,"
                + "`estado` ENUM('En curso', 'Finalizado', 'Cancelado') NOT NULL,"
                + "`fecha_inicio` DATE,"
                + "`fecha_fin` DATE,"
                + "FOREIGN KEY (`id_cliente`) REFERENCES `clientes`(`id_cliente`) ON DELETE SET NULL"
                + ")";
        stmt.executeUpdate(sqlProyectos);

        String sqlFacturas = "CREATE TABLE IF NOT EXISTS `facturas` ("
                + "`id_factura` INT PRIMARY KEY AUTO_INCREMENT,"
                + "`id_proyecto` INT,"
                + "`codigo_factura` VARCHAR(50) UNIQUE," // <-- LÍNEA AÑADIDA
                + "`monto` DECIMAL(10, 2) NOT NULL,"
                + "`fecha_emision` DATE,"
                + "`estado` VARCHAR(20) DEFAULT 'Pendiente',"
                + "FOREIGN KEY (`id_proyecto`) REFERENCES `proyectos`(`id_proyecto`) ON DELETE CASCADE"
                + ")";
        stmt.executeUpdate(sqlFacturas);
        
        String sqlEmpleados = "CREATE TABLE IF NOT EXISTS `empleados` ("
                + "`id_empleado` INT PRIMARY KEY AUTO_INCREMENT,"
                + "`nombre` VARCHAR(100) NOT NULL,"
                + "`cargo` VARCHAR(100),"
                + "`email` VARCHAR(100),"
                + "`telefono` VARCHAR(20)"
                + ")";
        stmt.executeUpdate(sqlEmpleados);

        String sqlTareas = "CREATE TABLE IF NOT EXISTS `tareas` ("
                + "`id_tarea` INT PRIMARY KEY AUTO_INCREMENT,"
                + "`id_proyecto` INT,"
                + "`id_empleado` INT,"
                + "`descripcion` TEXT NOT NULL,"
                + "`fecha_limite` DATE,"
                + "`estado` VARCHAR(20) DEFAULT 'Pendiente',"
                + "FOREIGN KEY (`id_proyecto`) REFERENCES `proyectos`(`id_proyecto`) ON DELETE CASCADE,"
                + "FOREIGN KEY (`id_empleado`) REFERENCES `empleados`(`id_empleado`) ON DELETE SET NULL"
                + ")";
        stmt.executeUpdate(sqlTareas);

        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS `usuarios` ("
                + "`id_usuario` INT PRIMARY KEY AUTO_INCREMENT,"
                + "`nombre_usuario` VARCHAR(50) NOT NULL UNIQUE,"
                + "`contrasena` VARCHAR(255) NOT NULL,"
                + "`rol` ENUM('Administrador', 'Gestor', 'Empleado') NOT NULL,"
                + "`id_empleado` INT UNIQUE," // <-- CAMPO AÑADIDO (UNIQUE para que un empleado solo tenga una cuenta)
                + "FOREIGN KEY (`id_empleado`) REFERENCES `empleados`(`id_empleado`) ON DELETE SET NULL" // <-- VÍNCULO
                + ")";
        stmt.executeUpdate(sqlUsuarios);

        String sqlGastos = "CREATE TABLE IF NOT EXISTS `gastos` ("
                + "`id_gasto` INT PRIMARY KEY AUTO_INCREMENT,"
                + "`id_proyecto` INT,"
                + "`descripcion` VARCHAR(255) NOT NULL,"
                + "`monto` DECIMAL(10, 2) NOT NULL,"
                + "`fecha` DATE,"
                + "FOREIGN KEY (`id_proyecto`) REFERENCES `proyectos`(`id_proyecto`) ON DELETE CASCADE"
                + ")";
        stmt.executeUpdate(sqlGastos);

        String sqlHoras = "CREATE TABLE IF NOT EXISTS `registros_horas` ("
                + "`id_registro` INT PRIMARY KEY AUTO_INCREMENT,"
                + "`id_tarea` INT,"
                + "`id_empleado` INT,"
                + "`horas` DECIMAL(5, 2) NOT NULL,"
                + "`fecha` DATE,"
                + "`descripcion` TEXT,"
                + "FOREIGN KEY (`id_tarea`) REFERENCES `tareas`(`id_tarea`) ON DELETE CASCADE,"
                + "FOREIGN KEY (`id_empleado`) REFERENCES `empleados`(`id_empleado`) ON DELETE SET NULL"
                + ")";
        stmt.executeUpdate(sqlHoras);
        
        // --- Insertar usuario admin por defecto si no existe ---
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuarios");
        rs.next();
        if (rs.getInt(1) == 0) {
            stmt.executeUpdate("INSERT INTO usuarios (nombre_usuario, contrasena, rol) VALUES ('admin', 'admin', 'Administrador')");
            System.out.println("Usuario 'admin' (contraseña 'admin') creado.");
        }
        
        stmt.close();
    }
}