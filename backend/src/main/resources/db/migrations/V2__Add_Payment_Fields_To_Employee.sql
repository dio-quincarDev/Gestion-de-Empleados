-- V2__Add_Payment_Fields_To_Employee.sql
-- Añadir campos de pago a la tabla employee para alinear con EmployeeEntity

ALTER TABLE employee 
ADD COLUMN payment_method_type VARCHAR(50),
ADD COLUMN phone_number VARCHAR(20),
ADD COLUMN bank_name VARCHAR(100),
ADD COLUMN account_number VARCHAR(50),
ADD COLUMN bank_account_type VARCHAR(50);

-- Asegurar que base_salary exista también
ALTER TABLE employee ADD COLUMN base_salary DECIMAL(19, 2) DEFAULT 0.00;