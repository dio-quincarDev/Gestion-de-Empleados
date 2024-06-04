CREATE TABLE IF NOT EXISTS Employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    salary DECIMAL(19, 2) NOT NULL CHECK (salary >= 0)
);

CREATE TABLE IF NOT EXISTS Consumption (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    consumption_date DATETIME NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(19, 2) NOT NULL CHECK (amount >= 0),
    employee_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES Employee(id)
);

CREATE TABLE IF NOT EXISTS Schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    employee_id BIGINT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES Employee(id)
);