# Sistema de Gestión de Empleados

## Descripción
API para la gestión integral de empleados, diseñada con una arquitectura hexagonal para garantizar la modularidad y mantenibilidad del código.

## Tecnologías

* **Backend:** Java 17, Spring Boot, MySQL
* **Herramientas:** Postman, Swagger, Git

[![Java](https://img.shields.io/badge/java-17-orange.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/spring%20boot-3.1.x-green.svg)](https://spring.io/projects/spring-boot/)
[![MySQL](https://img.shields.io/badge/mysql-8.0-blue.svg)](https://www.mysql.com/)
[![Spring](https://img.shields.io/badge/spring-framework-green.svg)](https://spring.io/)
[![Postman](https://img.shields.io/badge/postman-API%20testing-orange.svg)](https://www.postman.com/)
[![Swagger](https://img.shields.io/badge/swagger-API%20documentation-85EA2D.svg)](https://swagger.io/)
[![Git](https://img.shields.io/badge/git-version%20control-F05032.svg)](https://git-scm.com/)

## Arquitectura

* **Hexagonal:** Separación clara de las capas de dominio, aplicación y adaptación.
* **Capas:**
    * **Adapters:** Configuración, controladores, integraciones, DTOs, repositorios.
    * **Application:** Servicios de aplicación que coordinan la lógica de negocio.
    * **Domain:** Entidades del dominio, servicios, reglas de negocio, excepciones.
    * **Ports:** Contratos que definen las interfaces entre las capas.

## Funcionalidades

* Gestión de perfiles de empleados (alta, baja, modificación).
* Consulta de información detallada de empleados.
* Generación de reportes personalizados.
* Integración con sistemas de terceros (opcional).

## Configuración del Proyecto

### Requisitos Previos

- Java Development Kit (JDK) 17
- MySQL 8.0
- Git (para control de versiones)



## Documentación API

La documentación de la API está disponible a través de Swagger UI. Una vez que la aplicación esté en ejecución, puedes acceder a ella en:

```
http://localhost:8080/swagger-ui.html
```

## Contribución

1. Haz un fork del proyecto
2. Crea una nueva rama (`git checkout -b feature/nueva-funcionalidad`)
3. Realiza tus cambios y haz commit (`git commit -am 'Añadir nueva funcionalidad'`)
4. Haz push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crea un nuevo Pull Request

