-- V1__Initial_Schema.sql

-- Table: Employee
CREATE TABLE Employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE, -- Added email column with unique constraint
    role VARCHAR(50) NOT NULL,
    salary DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL, -- Added status column based on domain model
    CONSTRAINT chk_salary CHECK (salary >= 0) -- Moved from V4/V6
);

-- Table: ConsumptionClass
CREATE TABLE Consumption (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    consumption_date DATETIME NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(19, 2) NOT NULL,
    employee_id BIGINT,
    CONSTRAINT chk_amount CHECK (amount >= 0), -- Moved from V4
    FOREIGN KEY (employee_id) REFERENCES Employee(id)
);

-- Table: model.domain.com.bar-employees.com.employed.model.domain.ScheduleClass
CREATE TABLE model.domain.com.bar-employees.ScheduleClass (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    employee_id BIGINT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES Employee(id)
);

-- Table: Attendance_Records (from V5)
CREATE TABLE Attendance_Records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    entry_time TIME,
    entry_hour INT,
    entry_minute INT,
    exit_time TIME,
    exit_hour INT,
    exit_minute INT,
    status VARCHAR(50),
    employee_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES Employee(id)
);

-- Indexes (from V6)
CREATE INDEX idx_employee_name ON Employee(name);
CREATE INDEX idx_employee_role ON Employee(role);
CREATE INDEX idx_attendance_date ON Attendance_Records(date);
CREATE INDEX idx_consumption_date ON Consumption(consumption_date);
CREATE INDEX idx_schedule_start_time ON Schedule(start_time);

-- Constraints (from V6)
ALTER TABLE Attendance_Records ADD CONSTRAINT chk_status CHECK (status IN ('Present', 'Absent', 'Late'));
