-- V6__Update_Users_Table_Add_Name_Field.sql
-- Actualizar la tabla users para usar un solo campo name en lugar de firstname y lastname

-- Agregar el nuevo campo name
ALTER TABLE users ADD COLUMN IF NOT EXISTS name VARCHAR(100) NOT NULL DEFAULT '';

-- Actualizar el campo name combinando firstname y lastname existentes (esto requiere que existan las columnas antiguas)
-- En MySQL esto se hace con un UPDATE, pero en H2 se adapta
MERGE INTO users u
USING (SELECT id, CONCAT(firstname, ' ', lastname) AS full_name FROM users) AS src
ON u.id = src.id
WHEN MATCHED THEN UPDATE SET name = src.full_name;

-- Eliminar las columnas firstname y lastname
ALTER TABLE users DROP COLUMN IF EXISTS firstname;
ALTER TABLE users DROP COLUMN IF EXISTS lastname;