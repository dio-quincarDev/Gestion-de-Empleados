CREATE TABLE Consumption (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    consumption_date DATETIME,
    description VARCHAR(255),
    amount DECIMAL(19, 2),
    employee_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES Employee(id)
);