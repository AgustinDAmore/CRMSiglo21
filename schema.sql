-- Elimina la base de datos si ya existe para empezar desde cero (opcional)
DROP DATABASE IF EXISTS gestion_empresa;

-- Crea la base de datos
CREATE DATABASE IF NOT EXISTS gestion_empresa;

-- Selecciona la base de datos para usarla
USE gestion_empresa;

-- Creación de la tabla de clientes
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    empresa VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    telefono VARCHAR(25),
    estado ENUM('Activo', 'Inactivo', 'Potencial') NOT NULL,
    notas TEXT
);

-- Creación de la tabla de empleados
CREATE TABLE empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cargo VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    telefono VARCHAR(25)
);

-- Creación de la tabla de proyectos
CREATE TABLE proyectos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    id_cliente INT NOT NULL,
    estado ENUM('En curso', 'Finalizado', 'Cancelado') NOT NULL,
    fecha_inicio DATE,
    fecha_fin DATE,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id) ON DELETE CASCADE
);

-- Creación de la tabla de tareas
CREATE TABLE tareas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_proyecto INT NOT NULL,
    id_empleado INT NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_limite DATE,
    estado ENUM('Pendiente', 'En progreso', 'Completada') NOT NULL,
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_empleado) REFERENCES empleados(id)
);

-- Creación de la tabla de gastos
CREATE TABLE gastos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_proyecto INT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    fecha DATE NOT NULL,
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id) ON DELETE CASCADE
);

-- Creación de la tabla de facturas
CREATE TABLE facturas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_proyecto INT NOT NULL,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    fecha_emision DATE NOT NULL,
    estado ENUM('Pendiente', 'Pagada', 'Vencida') NOT NULL,
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id)
);

-- Creación de la tabla de registros de horas
CREATE TABLE registros_horas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_tarea INT NOT NULL,
    id_empleado INT NOT NULL,
    horas DECIMAL(5, 2) NOT NULL,
    fecha DATE NOT NULL,
    descripcion VARCHAR(255),
    FOREIGN KEY (id_tarea) REFERENCES tareas(id) ON DELETE CASCADE,
    FOREIGN KEY (id_empleado) REFERENCES empleados(id)
);

-- Creación de la tabla de usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado INT NOT NULL UNIQUE,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    rol ENUM('Administrador', 'Gestor', 'Empleado') NOT NULL,
    FOREIGN KEY (id_empleado) REFERENCES empleados(id) ON DELETE CASCADE
);