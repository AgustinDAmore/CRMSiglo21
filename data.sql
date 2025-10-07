-- Selecciona la base de datos para asegurarse de que los datos se insertan en el lugar correcto
USE gestion_empresa;

-- Insertar empleados de prueba
INSERT INTO empleados (id, nombre, cargo, email, telefono) VALUES
(1, 'Ana Torres', 'Gerente General', 'atorres@empresa.com', '111222333'),
(2, 'Carlos Rojas', 'Jefe de Proyectos', 'crojas@empresa.com', '444555666'),
(3, 'Elena Soto', 'Desarrolladora', 'esoto@empresa.com', '777888999'),
(4, 'Luis Marín', 'Analista de Marketing', 'lmarin@empresa.com', '000111222');

-- Insertar usuarios asociados a los empleados
-- NOTA: Las contraseñas aquí están en texto plano. En un entorno real, deberían estar "hasheadas".
INSERT INTO usuarios (id_empleado, nombre_usuario, contrasena, rol) VALUES
(1, 'atorres', 'Admin123!', 'Administrador'),
(2, 'crojas', 'Gestor456*', 'Gestor'),
(3, 'esoto', 'Empleado789.', 'Empleado'),
(4, 'lmarin', 'Empleado012.', 'Empleado');

-- Insertar clientes
INSERT INTO clientes (id, nombre, empresa, email, telefono, estado, notas) VALUES
(1, 'Tech Solutions SRL', 'Tech Solutions', 'contacto@techsolutions.com', '123456789', 'Activo', 'Cliente importante, contactar mensualmente.'),
(2, 'Innovate Marketing', 'Innovate Marketing', 'info@innovate.com', '987654321', 'Activo', 'Campaña de fin de año en discusión.'),
(3, 'Consultora Global', 'Consultora Global', 'admin@global.com', '555555555', 'Potencial', 'Solicitaron presupuesto, pendiente de respuesta.');

-- Insertar proyectos
INSERT INTO proyectos (id, nombre, descripcion, id_cliente, estado, fecha_inicio, fecha_fin) VALUES
(1, 'Desarrollo de CRM', 'Sistema de gestión de clientes para Tech Solutions', 1, 'En curso', '2025-09-01', '2025-12-20'),
(2, 'Campaña Publicitaria Q4', 'Campaña de marketing digital para fin de año', 2, 'Finalizado', '2025-10-01', '2025-10-31'),
(3, 'Mantenimiento Sistema Legado', 'Soporte técnico y corrección de bugs para sistema contable', 1, 'En curso', '2025-08-15', NULL);

-- Insertar tareas
INSERT INTO tareas (id, id_proyecto, id_empleado, descripcion, fecha_limite, estado) VALUES
(1, 1, 2, 'Analizar Requerimientos y definir alcance', '2025-09-15', 'Completada'),
(2, 1, 3, 'Desarrollar módulo de autenticación de usuarios', '2025-10-10', 'En progreso'),
(3, 2, 4, 'Definir estrategia y pauta en redes sociales', '2025-10-05', 'Completada'),
(4, 2, 4, 'Diseñar las creatividades y copys para anuncios', '2025-10-12', 'Completada'),
(5, 3, 3, 'Corregir bug en la generación de reportes de ventas', '2025-11-05', 'Pendiente');

-- Insertar facturas
INSERT INTO facturas (id, id_proyecto, codigo, monto, fecha_emision, estado) VALUES
(1, 1, 'FACT-001-2025', 15000.00, '2025-09-30', 'Pendiente'),
(2, 2, 'FACT-002-2025', 7500.50, '2025-11-01', 'Pagada');

-- Insertar gastos
INSERT INTO gastos (id, id_proyecto, descripcion, monto, fecha) VALUES
(1, 1, 'Compra de licencia de software de desarrollo', 800.00, '2025-09-05'),
(2, 2, 'Inversión en pauta publicitaria para Facebook Ads', 2500.00, '2025-10-15');

-- Insertar registros de horas
INSERT INTO registros_horas (id, id_tarea, id_empleado, horas, fecha, descripcion) VALUES
(1, 1, 2, 25.0, '2025-09-14', 'Reuniones con el cliente y documentación de requerimientos.'),
(2, 2, 3, 40.5, '2025-10-09', 'Implementación de hashing de contraseñas y lógica de login.'),
(3, 3, 4, 16.0, '2025-10-04', 'Análisis de audiencia y configuración de campaña.');
