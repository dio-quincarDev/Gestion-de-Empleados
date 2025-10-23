-- V1__Initial_Schema.sql - Final Consolidated Version
-- #################################
-- 1. Tabla: users (Autenticación y Perfil de Acceso)
-- #################################
-- Se separa del empleado para un mejor control de seguridad (quién tiene acceso vs. quién trabaja).
CREATE TABLE IF NOT EXISTS users (
    id CHAR(36) PRIMARY KEY, -- Usamos CHAR(36) para UUID (consistente con UserEntity)
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL, -- Corresponde al rol de seguridad/autorización
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- Para deshabilitar la cuenta sin eliminar el registro
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_user_email ON users(email);


-- #################################
-- 2. Tabla: employee (Datos de RRHH)
-- #################################
-- Incluye toda la información de RRHH. Relacionado 1:1 con 'users'.
CREATE TABLE IF NOT EXISTS employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id CHAR(36) UNIQUE, -- FK a la cuenta de usuario (1:1)
    name VARCHAR(100) NOT NULL, -- Nombre completo (si se prefiere separado usar first_name/last_name)
    email VARCHAR(255) NOT NULL UNIQUE,
    contact_phone VARCHAR(20) UNIQUE,
    role VARCHAR(50) NOT NULL, -- El rol en el bar (e.g., BARTENDER, COOK)

    -- Tarifas y Pago
    payment_type VARCHAR(50) NOT NULL, -- HOURLY, SALARY
    base_salary DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    hourly_rate DECIMAL(19, 2) NOT NULL DEFAULT 0.00,

    -- Horas Extra
    pays_overtime BOOLEAN NOT NULL DEFAULT FALSE,
    overtime_rate_type VARCHAR(50) NULL,

    -- Status y Auditoría
    status VARCHAR(50) NOT NULL, -- ACTIVO, INACTIVO, VACACIONES
    hire_date DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_employee_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_salary_positive CHECK (base_salary >= 0 AND hourly_rate >= 0)
);

-- Índices
CREATE INDEX idx_employee_role ON employee(role);


-- #################################
-- 3. Tabla: payment_details (Normalización de Pago)
-- #################################
-- Normaliza la información de pago, permitiendo múltiples métodos.
CREATE TABLE IF NOT EXISTS payment_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    payment_method_type VARCHAR(50) NOT NULL, -- YAPPY, ACH, CASH
    is_default BOOLEAN NOT NULL DEFAULT TRUE, -- Indica el método principal
    bank_name VARCHAR(100) NULL,
    account_number VARCHAR(50) NULL,
    phone_number VARCHAR(20) NULL,
    bank_account_type VARCHAR(50) NULL,
    percentage_split DECIMAL(5, 2) NOT NULL DEFAULT 100.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (employee_id) REFERENCES employee(id)
);


-- #################################
-- 4. Tabla: schedule (Planificación de Turnos)
-- #################################
-- Se añade zona horaria y auditoría.
CREATE TABLE IF NOT EXISTS schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    shift_name VARCHAR(100) NULL, -- Ej: "Turno de Cierre"
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    time_zone VARCHAR(50) NULL, -- e.g., 'America/Panama'
    is_published BOOLEAN NOT NULL DEFAULT FALSE, -- Para diferenciar borradores de turnos confirmados
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (employee_id) REFERENCES employee(id)
);

-- Índices
CREATE INDEX idx_schedule_employee_start ON schedule(employee_id, start_time);


-- #################################
-- 5. Tabla: attendance_records (Registro de Asistencia/Reloj)
-- #################################
-- Se añade trazabilidad y zona horaria.
CREATE TABLE IF NOT EXISTS attendance_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
        recorded_by_user_id CHAR(36) NULL, -- FK si fue registrado por un administrador
    
        entry_date_time DATETIME NULL,
        exit_date_time DATETIME NULL,
        time_zone VARCHAR(50) NULL, -- e.g., 'America/Panama'
    
        status VARCHAR(50) NOT NULL, -- PRESENT, LATE, ABSENT (ENUM en Java)
        device_used VARCHAR(50) NULL, -- Terminal, Mobile, Web
    
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (employee_id) REFERENCES employee(id),
    FOREIGN KEY (recorded_by_user_id) REFERENCES users(id)
);

-- Índices
CREATE INDEX idx_attendance_employee_entry ON attendance_records(employee_id, entry_date_time);


-- #################################
-- 6. Tabla: consumption (Consumos de Empleados)
-- #################################
-- Se añade control de negocio (aprobación) y auditoría.
CREATE TABLE IF NOT EXISTS consumption (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT,
    consumption_date DATETIME NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(19, 2) NOT NULL,
    is_approved BOOLEAN NOT NULL DEFAULT FALSE, -- Flujo de trabajo: debe ser aprobado para descuento
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (employee_id) REFERENCES employee(id),
    CONSTRAINT chk_consumption_amount CHECK (amount >= 0)
);

-- Índices
CREATE INDEX idx_consumption_employee_date ON consumption(employee_id, consumption_date);