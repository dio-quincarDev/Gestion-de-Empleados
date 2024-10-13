# Sistema de Gestión de Empleados

## Descripción
API para la gestión integral de empleados, diseñada con una arquitectura hexagonal para garantizar la modularidad y mantenibilidad del código.

## Tecnologías y Herramientas

[![Java](https://img.shields.io/badge/-Java-007396?style=flat-square&logo=java&logoColor=white)](https://www.java.com/)
[![Spring](https://img.shields.io/badge/-Spring-6DB33F?style=flat-square&logo=spring&logoColor=white)](https://spring.io/)
[![MySQL](https://img.shields.io/badge/-MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Git](https://img.shields.io/badge/-Git-F05032?style=flat-square&logo=git&logoColor=white)](https://git-scm.com/)
[![Postman](https://img.shields.io/badge/-Postman-FF6C37?style=flat-square&logo=postman&logoColor=white)](https://www.postman.com/)
[![Swagger](https://img.shields.io/badge/-Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)](https://swagger.io/)

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
- Git


## Documentación API

La documentación de la API está disponible a través de Swagger UI. Una vez que la aplicación esté en ejecución, puedes acceder a ella en:



## Contribución

1. Haz un fork del proyecto
2. Crea una nueva rama (`git checkout -b feature/nueva-funcionalidad`)
3. Realiza tus cambios y haz commit (`git commit -am 'Añadir nueva funcionalidad'`)
4. Haz push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crea un nuevo Pull Request
