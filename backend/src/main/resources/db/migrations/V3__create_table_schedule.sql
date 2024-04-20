CREATE TABLE Schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time DATETIME,
    end_time DATETIME,
    employee_id BIGINT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES Employee(id)
);