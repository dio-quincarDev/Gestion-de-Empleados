CREATE TABLE  IF NOT EXISTS Attendance_Records (
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
    FOREIGN KEY (employee_id) REFERENCES Employee(id),
    INDEX (employee_id)
);