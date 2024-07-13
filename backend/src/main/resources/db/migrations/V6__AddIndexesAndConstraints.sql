CREATE INDEX idx_employee_name ON Employee(name);
CREATE INDEX idx_employee_role ON Employee(role);
CREATE INDEX idx_attendance_date ON Attendance_Records(date);
CREATE INDEX idx_consumption_date ON Consumption(consumption_date);
CREATE INDEX idx_schedule_start_time ON Schedule(start_time);


ALTER TABLE Attendance_Records ADD CONSTRAINT chk_status CHECK (status IN ('Present', 'Absent', 'Late'));


ALTER TABLE Employee MODIFY salary DECIMAL(19, 2) CHECK (salary >= 0);