# Proyecto de Gestión de Empleados

## Descripción

Este es un proyecto personal de gestión de empleados desarrollado con Java 17, Spring Boot y MySQL. El proyecto sigue una arquitectura hexagonal y actualmente se encuentra en desarrollo.

## Tecnologías

- Java 17
- Spring Boot
- MySQL

## Arquitectura

El proyecto sigue una arquitectura hexagonal con las siguientes capas:

### Capa Adapters

Esta capa contiene los siguientes paquetes:

- `config`: Contiene las clases `ApiDocConfig`, `PersistenceConfig`, `WebConfig`.
- `controllers`: Contiene los controladores `ConsumptionControllers`, `EmployeeControllers`, `ReportController`, `ScheduleController`.
- `integrations`: Contiene los servicios `EmailService`, `NotificationService`.
- `repositories`: Contiene los repositorios `JpaConsumptionRepository`, `JpaEmployeeRepository`, `JpaScheduleRepository`.
- `dto`: Contiene los DTOs `ConsumptionDto`, `EmployeeDto`, `ScheduleDto`, `AttendanceReportDto`, `ConsumptionReportDto`, `ReportDto`.

### Capa Application

Esta capa contiene los siguientes servicios de aplicación:

- `ConsumptionApplicationService`
- `ReportingApplicationService`
- `EmployeeApplicationService`
- `ScheduleApplicationService`

### Capa Domain

Esta capa contiene los siguientes paquetes:

- `exceptions`: Contiene las excepciones `EmployeeNotFoundExceptions`, `InvalidConsumptionDataExceptions`, `InvalidScheduleException`.
- `model`: Contiene los modelos `AttendanceRecord`, `Consumption`, `Employee`, `Schedule`.
- `services`: Contiene los servicios `AttendanceCalculationService`, `ConsumptionCalculationService`, `ReportingServices`.
- `servicesImpl`: Contiene las implementaciones de los servicios `AttendanceCalculationServiceImpl`, `ConsumptionCalculationServiceImpl`, `ReportingServiceImpl`.

### Capa Ports

Esta capa contiene los siguientes paquetes:

- `in`: Contiene los servicios `AttendaceCalculationService`, `ConsumptionCalculationService`, `ConsumptionRepository`, `EmployeeRepository`, `ScheduleRepository`, `ReportingAdapter`.
- `out`: Contiene los servicios `AttendanceReportService`, `ConsumptionReportService`, `ReportingPort`.

## Base de Datos

La base de datos `Gestion` contiene las siguientes tablas:

- `attendance_records`: Contiene las columnas `id`, `date`, `entry_time`, `exit_time`, `status`, `employee_id`.
- `employee`: Contiene las columnas `id`, `name`, `role`, `salary`.
- `consumption`: Contiene las columnas `id`, `consumption_date`, `description`, `amount`, `employee_id`.
- `schedule`: Contiene las columnas `id`, `start_time`, `exit_time`, `employee_id`.
- `employee_seq`: Contiene la columna `nex_val`.

## Frontend

Actualmente, estoy considerando si implementar una aplicación móvil híbrida o una aplicación web detallada para el frontend. Cualquier sugerencia o contribución es bienvenida.

## Contribuciones

Como este es un proyecto en desarrollo, cualquier contribución es bienvenida. Por favor, abre un issue para discutir lo que te gustaría cambiar.

## Licencia

MIT
