-- V5__Ensure_Foreign_Key_Compatibility_After_Name_Change.sql
-- Asegurar la compatibilidad de las claves foráneas después de los cambios en la estructura

-- Asegurar que la columna id en users tenga el tipo correcto
ALTER TABLE users 
ALTER COLUMN id CHAR(36);

-- Asegurar que la columna user_id en employee tenga el tipo correcto
ALTER TABLE employee 
ALTER COLUMN user_id CHAR(36);

-- Si es necesario, recrear la clave foránea con compatibilidad
-- Primero eliminar si existe
ALTER TABLE employee DROP CONSTRAINT IF EXISTS fk_employee_user;

-- Luego crearla nuevamente
ALTER TABLE employee 
ADD CONSTRAINT fk_employee_user FOREIGN KEY (user_id) REFERENCES users(id);