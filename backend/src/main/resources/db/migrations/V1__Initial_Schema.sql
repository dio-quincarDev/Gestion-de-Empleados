-- V1__Initial_Schema.sql - Final Consolidated Version

-- Table: employee
CREATE TABLE employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    salary DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT chk_salary CHECK (salary >= 0)
);

-- Table: consumption
CREATE TABLE consumption (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    consumption_date DATETIME NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(19, 2) NOT NULL,
    employee_id BIGINT,
    CONSTRAINT chk_amount CHECK (amount >= 0),
    FOREIGN KEY (employee_id) REFERENCES employee(id)
);

-- Table: schedule
CREATE TABLE schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    employee_id BIGINT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employee(id)
);

-- Table: attendance_records
CREATE TABLE attendance_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entry_date_time DATETIME NULL,
    exit_date_time DATETIME NULL,
    status ENUM('PRESENT', 'LATE', 'ABSENT'),
    employee_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES employee(id)
);

-- Indexes
CREATE INDEX idx_employee_name ON employee(name);
CREATE INDEX idx_employee_role ON employee(role);
CREATE INDEX idx_consumption_date ON consumption(consumption_date);
CREATE INDEX idx_schedule_start_time ON schedule(start_time);
