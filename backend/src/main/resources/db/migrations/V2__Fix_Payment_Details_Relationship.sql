-- V2__Fix_Payment_Details_Relationship.sql
-- Esta migración corrige la relación entre employee y payment_details
-- para resolver el error 'Unknown column' en consultas JOIN FETCH con paginación
-- y establece la estructura para evitar problemas de fetching en JPA

-- Asegurar índices para mejorar rendimiento de JOINs
CREATE INDEX IF NOT EXISTS idx_payment_details_employee_id ON payment_details(employee_id);
CREATE INDEX IF NOT EXISTS idx_payment_details_is_default ON payment_details(is_default);

-- Asegurar que no haya registros inconsistentes que puedan causar problemas
-- Establecer un valor por defecto para is_default si hay múltiples registros sin valor
UPDATE payment_details SET is_default = FALSE WHERE is_default IS NULL;

-- Asegurar que cada empleado que tenga payment_details tenga al menos uno marcado como default
-- (esto se hará con procedimiento lógico, no con una migración directa de SQL compleja)

-- Importante: para resolver el problema de Hibernate, se debe mantener la integridad
-- de la relación pero evitar JOIN FETCH en consultas de paginación en el código Java