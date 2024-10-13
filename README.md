# Gestión de Empleados

## Descripción
API para la gestión integral de empleados, diseñada con una arquitectura hexagonal para garantizar la modularidad y mantenibilidad del código.

## Tecnologías
* **Backend:** Java 17, Spring Boot, MySQL
* **Herramientas:** Postman, Swagger, Git, Maven/Gradle
* **Frontend:** [Especificar aquí el framework utilizado, por ejemplo, React, Angular, Vue]

[![Java](https://img.shields.io/badge/java-17-orange.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/spring%20boot-6.0.x-green.svg)](https://spring.io/projects/spring-boot/)
[![MySQL](https://img.shields.io/badge/mysql-latest-blue.svg)](https://www.mysql.com/)
[![Postman](https://img.io/badge/postman-API-blueviolet.svg)](https://www.postman.com/)
[![Swagger](https://img.shields.io/badge/swagger-API-blue.svg)](https://swagger.io/)
[![Git](https://img.shields.io/badge/git-version%20control-brightgreen.svg)](https://git-scm.com/)
[![Maven](https://img.shields.io/badge/maven-build-blue.svg)](https://maven.apache.org/)  [![Frontend Framework](https://img.shields.io/badge/frontend-React-blue.svg)](https://reactjs.org/)  ## Arquitectura
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


   
