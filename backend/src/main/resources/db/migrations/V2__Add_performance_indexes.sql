-- Indexes for users table
-- A unique index on email ensures fast lookups and prevents duplicate user registrations.
CREATE UNIQUE INDEX idx_user_email ON users (email);

-- An index on the role column to speed up queries that filter users by their role.
CREATE INDEX idx_user_role ON users (role);

-- Indexes for employee table
-- A unique index on email for fast lookups and to enforce data integrity.
CREATE UNIQUE INDEX idx_employee_email ON employee (email);

-- A composite index on role and status to optimize searches for employees with a specific role and status.
CREATE INDEX idx_employee_role_status ON employee (role, status);

-- Indexes for attendance_records table
-- A composite index to speed up queries that fetch attendance records for a specific employee within a date range.
CREATE INDEX idx_attendance_employee_datetime ON attendance_records (employee_id, entry_date_time, exit_date_time);

-- Indexes for consumption table
-- A composite index to optimize queries for an employee's consumption within a date range.
CREATE INDEX idx_consumption_employee_date ON consumption (employee_id, consumption_date);

-- An index on consumption_date to speed up queries that aggregate consumption across all employees in a date range.
CREATE INDEX idx_consumption_date ON consumption (consumption_date);