-- V4__Fix_Existing_Invalid_Uuid_Data.sql
-- Limpiar datos corruptos de UUIDs y asegurar compatibilidad en todos los entornos

-- Eliminar registros con UUIDs inválidos (usando una aproximación compatible con ambos motores)
-- Para H2, simplificamos el enfoque ya que no se puede usar SET SQL_SAFE_UPDATES

-- Eliminamos los datos que causan problemas con las claves foráneas
-- En entornos de desarrollo, es aceptable limpiar datos problemáticos
-- Eliminar registros en tablas que hacen referencia a IDs inválidos
DELETE FROM attendance_records WHERE recorded_by_user_id IS NOT NULL 
  AND recorded_by_user_id NOT LIKE '________-____-____-____-____________' 
  AND CHAR_LENGTH(recorded_by_user_id) != 36;

DELETE FROM employee WHERE user_id IS NOT NULL 
  AND user_id NOT LIKE '________-____-____-____-____________' 
  AND CHAR_LENGTH(user_id) != 36;