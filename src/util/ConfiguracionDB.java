package util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConfiguracionDB {

    public static void crearTablasSiNoExisten(Connection conexion) {
        try (Statement stmt = conexion.createStatement()) {
            // Tabla de Clientes
            String sqlClientes = "CREATE TABLE IF NOT EXISTS clientes ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "nombre VARCHAR(255) NOT NULL,"
                + "empresa VARCHAR(255),"
                + "email VARCHAR(255),"
                + "telefono VARCHAR(50),"
                + "estado VARCHAR(50),"
                + "notas TEXT"
                + ");";
            stmt.execute(sqlClientes);

            // Tabla de Empleados
            String sqlEmpleados = "CREATE TABLE IF NOT EXISTS empleados ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "nombre VARCHAR(255) NOT NULL,"
                + "cargo VARCHAR(100),"
                + "email VARCHAR(255),"
                + "telefono VARCHAR(50)"
                + ");";
            stmt.execute(sqlEmpleados);

            // Tabla de Proyectos
            String sqlProyectos = "CREATE TABLE IF NOT EXISTS proyectos ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "nombre VARCHAR(255) NOT NULL,"
                + "descripcion TEXT,"
                // CORRECCIÓN: Se cambió idCliente a id_cliente
                + "id_cliente INT,"
                + "estado VARCHAR(50),"
                + "fecha_inicio DATE,"
                + "fecha_fin DATE,"
                + "FOREIGN KEY (id_cliente) REFERENCES clientes(id) ON DELETE CASCADE"
                + ");";
            stmt.execute(sqlProyectos);

            // Tabla de Tareas
            String sqlTareas = "CREATE TABLE IF NOT EXISTS tareas ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                // CORRECCIÓN: Se cambiaron los nombres de las columnas a snake_case
                + "id_proyecto INT,"
                + "id_empleado INT,"
                + "descripcion TEXT,"
                + "fecha_limite DATE,"
                + "estado VARCHAR(50),"
                + "FOREIGN KEY (id_proyecto) REFERENCES proyectos(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (id_empleado) REFERENCES empleados(id)"
                + ");";
            stmt.execute(sqlTareas);

            // Tabla de Gastos
            String sqlGastos = "CREATE TABLE IF NOT EXISTS gastos ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                // CORRECCIÓN: Se cambió idProyecto a id_proyecto
                + "id_proyecto INT,"
                + "descripcion TEXT,"
                + "monto DECIMAL(10, 2),"
                + "fecha DATE,"
                + "FOREIGN KEY (id_proyecto) REFERENCES proyectos(id) ON DELETE CASCADE"
                + ");";
            stmt.execute(sqlGastos);

            // Tabla de Facturas
            String sqlFacturas = "CREATE TABLE IF NOT EXISTS facturas ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "codigo VARCHAR(255) UNIQUE,"
                // CORRECCIÓN: Se cambió idProyecto a id_proyecto
                + "id_proyecto INT,"
                + "monto DECIMAL(10, 2),"
                + "fecha_emision DATE,"
                + "estado VARCHAR(50),"
                + "FOREIGN KEY (id_proyecto) REFERENCES proyectos(id)"
                + ");";
            stmt.execute(sqlFacturas);

            // Tabla de Registro de Horas
            String sqlRegistroHoras = "CREATE TABLE IF NOT EXISTS registros_horas ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                // CORRECCIÓN: Se cambiaron los nombres de las columnas a snake_case
                + "id_tarea INT,"
                + "id_empleado INT,"
                + "horas DECIMAL(5, 2),"
                + "fecha DATE,"
                + "descripcion TEXT,"
                + "FOREIGN KEY (id_tarea) REFERENCES tareas(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (id_empleado) REFERENCES empleados(id)"
                + ");";
            stmt.execute(sqlRegistroHoras);
            
            // Tabla de Usuarios
            String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                // CORRECCIÓN: Se cambió idEmpleado a id_empleado
                + "id_empleado INT UNIQUE,"
                + "nombre_usuario VARCHAR(255) UNIQUE,"
                + "contrasena VARCHAR(255),"
                + "rol VARCHAR(50),"
                + "FOREIGN KEY (id_empleado) REFERENCES empleados(id) ON DELETE CASCADE"
                + ");";
            stmt.execute(sqlUsuarios);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
