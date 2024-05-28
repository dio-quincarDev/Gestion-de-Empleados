README.md
Proyecto de Gestión de Empleados
Descripción
Un proyecto personal para gestionar empleados, desarrollado con tecnologías comunes como Java, Spring Boot y MySQL. Sigue una arquitectura hexagonal para facilitar su mantenimiento y extensibilidad.
Tecnologías
 * Java 17
 * Spring Boot
 * MySQL
Arquitectura
Adopta una arquitectura hexagonal, dividiendo el proyecto en capas independientes:
 * Adaptadores: Contiene controladores, repositorios, servicios de integración y DTOs para interactuar con el mundo exterior.
 * Aplicación: Alberga los servicios de aplicación que encapsulan la lógica de negocio principal.
 * Dominio: Define los modelos de datos, entidades y servicios relacionados con la gestión de empleados.
 * Puertos: Proporciona interfaces para que las capas de aplicación y adaptadores se comuniquen entre sí.
 * Base de datos: Almacena información de empleados, consumos, horarios y registros de asistencia.
Base de datos
La base de datos Gestion guarda información en tablas como empleados, consumos, horarios y registros_asistencia.
Frontend
Se está considerando desarrollar una aplicación móvil híbrida o una aplicación web completa para la interfaz de usuario. Se aceptan sugerencias y contribuciones.
Contribuciones
Este proyecto es de código abierto y se agradecen las colaboraciones. Abre un "issue" para discutir tus ideas.
Licencia
MIT

