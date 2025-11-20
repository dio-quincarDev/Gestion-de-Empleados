-- V2__Add_Missing_Payment_Fields_Safe.sql
-- Añadir campos de pago a la tabla employee (compatible con MySQL y H2)

-- Añadir campos de pago (la migración fallará si ya existen, lo cual es aceptable en este contexto)
-- En entornos reales, los campos se añadirían si no existen, pero para mantener compatibilidad
-- entre MySQL y H2, añadimos directamente asumiendo que no existen aún

ALTER TABLE employee ADD COLUMN IF NOT EXISTS payment_method_type VARCHAR(50);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS bank_name VARCHAR(100);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS account_number VARCHAR(50);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS bank_account_type VARCHAR(50);