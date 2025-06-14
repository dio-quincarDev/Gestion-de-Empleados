# 🚀 **Sistema Inteligente de Gestión de Empleados**

### Un Bar Moderno en David, Chiriquí, Panamá

<div align="center">
  <img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17+">
  <img src="https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Data JPA">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Flyway-9.22.3-CC0200?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway">
  <img src="https://img.shields.io/badge/Spring_Mail-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Mail">
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white" alt="Thymeleaf">
</div>

-----

## 💡 **Acerca del Proyecto**

Sistema backend desarrollado con **Java 17+** y **Spring Boot 3.2.3** para la gestión integral del personal en el bar "La Esquina Bohemia". Automatiza procesos clave como:

- Administración completa de empleados
- Control detallado de consumos internos
- Registro preciso de asistencia
- Planificación flexible de horarios
- Generación automática de reportes por email

-----

## ✨ **Características Principales**

### 👥 Gestión de Empleados
- CRUD completo con validaciones avanzadas
- Búsqueda por múltiples criterios (estado, rol, nombre)
- Manejo de salarios y estados de empleados

### 🍹 Control de Consumos
- Registro detallado con fecha, descripción y monto
- Cálculo de totales por empleado o períodos específicos
- Historial completo de consumos

### ⏱️ Registro de Asistencia
- Sistema de check-in/check-out
- Cálculo automático de horas trabajadas
- Reportes de asistencia diaria/semanal

### 📅 Planificación de Horarios
- Asignación flexible de turnos
- Validación de conflictos de horarios
- Visualización clara de calendarios

### ✉️ Reportes Automatizados
- Generación semanal automática
- Plantillas HTML profesionales con Thymeleaf
- Envío asíncrono por email

-----

## ⚙️ **Arquitectura del Sistema**

```mermaid
graph TD
    A[API REST] --> B[Controladores]
    B --> C[Servicios de Aplicación]
    C --> D[Servicios de Dominio]
    C --> E[Repositorios JPA]
    C --> F[Email Service]
    D --> G[Entidades]
    E --> H[(MySQL)]
    F --> I[SMTP]
    F --> J[Thymeleaf]
```

**Flujo típico:**
1. Petición HTTP llega al controlador
2. Servicio de aplicación orquesta la operación
3. Servicio de dominio ejecuta lógica de negocio
4. Repositorio persiste/recupera datos
5. Respuesta estructurada retornada al cliente

-----

## 🛠️ **Tecnologías Principales**

<div align="center">
  <table>
    <tr>
      <td align="center" width="96">
        <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" width="48" height="48" alt="Java" />
        <br>Java 17+
      </td>
      <td align="center" width="96">
        <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" width="48" height="48" alt="Spring Boot" />
        <br>Spring Boot 3.2
      </td>
      <td align="center" width="96">
        <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mysql/mysql-original.svg" width="48" height="48" alt="MySQL" />
        <br>MySQL 8.0
      </td>
    </tr>
  </table>
</div>

-----

## 🚀 **Comenzando**

### Prerrequisitos
- JDK 17+
- Maven 3.6+
- MySQL 8.x (opcional)

### Configuración Inicial
1. Clonar repositorio:
   ```bash
   git clone https://github.com/tu-usuario/bar-management-system.git
   cd bar-management-system
   ```

2. Configurar base de datos (en `application.properties`):
   ```properties
   # Para desarrollo con H2
   spring.datasource.url=jdbc:h2:mem:testdb
   
   # Para producción con MySQL
   spring.datasource.url=jdbc:mysql://localhost:3306/bar_db
   spring.datasource.username=usuario
   spring.datasource.password=contraseña
   spring.flyway.locations=classpath:/db/migration
   ```

3. Configurar email (ejemplo para Gmail):
   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=tu-email@gmail.com
   spring.mail.password=app-password
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

### Ejecución
```bash
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080/api`

-----

## 📊 **Modelo de Datos**

```mermaid
erDiagram
    EMPLOYEE ||--o{ CONSUMPTION : "1-N"
    EMPLOYEE ||--o{ ATTENDANCE : "1-N"
    EMPLOYEE ||--o{ SCHEDULE : "1-N"
    
    EMPLOYEE {
        Long id PK
        String name
        String role
        String email
        BigDecimal salary
        String status
    }
    
    CONSUMPTION {
        Long id PK
        DateTime consumptionDate
        String description
        BigDecimal amount
        Long employeeId FK
    }
    
    ATTENDANCE {
        Long id PK
        Date date
        Time entryTime
        Time exitTime
        String status
        Long employeeId FK
    }
    
    SCHEDULE {
        Long id PK
        DateTime startTime
        DateTime endTime
        Long employeeId FK
    }
```

-----

## 🌟 **Endpoints Clave**

### Empleados (`/api/employees`)
| Método | Ruta | Descripción | Parámetros |
|--------|------|-------------|------------|
| POST   | /    | Crear empleado | EmployeeDto |
| GET    | /{id} | Obtener empleado | ID |
| GET    | /search | Búsqueda | name, role, status |
| PUT    | /{id} | Actualizar | ID, EmployeeDto |
| DELETE | /{id} | Eliminar | ID |

### Consumos (`/api/consumptions`)
| Método | Ruta | Descripción | Parámetros |
|--------|------|-------------|------------|
| POST   | /    | Registrar consumo | ConsumptionDto |
| GET    | /total | Total por empleado | employeeId, startDate, endDate |
| GET    | /total/all | Total general | startDate, endDate |

### Asistencia (`/api/attendances`)
| Método | Ruta | Descripción | Parámetros |
|--------|------|-------------|------------|
| POST   | /    | Registrar asistencia | AttendanceDto |
| GET    | /report | Generar reporte | employeeId, year, month, day |

-----

## 🛡️ **Seguridad y Validación**

**Validaciones implementadas:**
```java
@NotBlank(message = "Nombre es obligatorio")
private String name;

@Email(message = "Email inválido")
private String email;

@Positive(message = "Salario debe ser positivo")
private BigDecimal salary;

@Future(message = "Fecha debe ser futura")
private LocalDate startDate;
```

**Manejo de errores:**
- `400 Bad Request`: Validaciones fallidas
- `404 Not Found`: Recurso no existe
- `409 Conflict`: Violación de reglas de negocio
- `500 Internal Server Error`: Errores inesperados

-----

## 🤝 **Cómo Contribuir**

1. Haz fork del repositorio
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Realiza tus cambios y commitea
4. Haz push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

**Requisitos para contribuciones:**
- Incluir tests unitarios para nuevas funcionalidades
- Mantener consistencia de estilo de código
- Documentar cualquier nuevo endpoint en el README
- Actualizar la documentación de cambios importantes

-----

## 📄 **Licencia**

Distribuido bajo licencia MIT. Ver `LICENSE.md` para detalles.

```text
Copyright 2023 La Esquina Bohemia

Permiso otorgado para usar, copiar, modificar y distribuir este software 
y su documentación para cualquier propósito con o sin costo, siempre que 
el aviso de copyright anterior y este permiso aparezcan en todas las copias.
```

-----

<div align="center">
  <img src="https://img.shields.io/badge/Hecho%20con-❤️-ff69b4?style=for-the-badge" alt="Hecho con amor">
</div>
