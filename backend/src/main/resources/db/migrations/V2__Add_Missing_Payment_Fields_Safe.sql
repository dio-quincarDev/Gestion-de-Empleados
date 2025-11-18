-- V2__Add_Missing_Payment_Fields_Safe.sql
-- Añadir solo campos de pago que no existen en la tabla employee para alinear con EmployeeEntity

-- Añadir campos de pago solo si no existen
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'employee' 
AND COLUMN_NAME = 'payment_method_type';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE employee ADD COLUMN payment_method_type VARCHAR(50)', 
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'employee' 
AND COLUMN_NAME = 'phone_number';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE employee ADD COLUMN phone_number VARCHAR(20)', 
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'employee' 
AND COLUMN_NAME = 'bank_name';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE employee ADD COLUMN bank_name VARCHAR(100)', 
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'employee' 
AND COLUMN_NAME = 'account_number';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE employee ADD COLUMN account_number VARCHAR(50)', 
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'employee' 
AND COLUMN_NAME = 'bank_account_type';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE employee ADD COLUMN bank_account_type VARCHAR(50)', 
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;