-- V2__Remove_base_salary_column.sql
-- Elimina la columna base_salary redundante de la tabla employee

-- Para evitar errores en bases de datos donde la columna ya fue eliminada,
-- se sugiere verificar manualmente la existencia de la columna antes de aplicar este script
-- en entornos donde ya se haya creado la base con el schema correcto

-- Eliminar la columna base_salary de la tabla employee si existe
-- Nota: En algunos sistemas de migración, se requiere verificar manualmente la existencia
-- de la columna antes de eliminarla para evitar errores si ya fue eliminada previamente

ALTER TABLE employee DROP COLUMN  base_salary;