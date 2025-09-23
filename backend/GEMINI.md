## Project Overview

This is a Spring Boot application for managing employees in a bar. It provides functionalities for tracking attendance, consumption, and calculating payments. The application uses a hexagonal architecture, with a clear separation between the domain, application, and infrastructure layers.

**Main Technologies:**

*   **Backend:** Java 17, Spring Boot 3.2.3
*   **Database:** MySQL with Flyway for migrations
*   **Authentication:** JWT (JSON Web Tokens)
*   **API Documentation:** OpenAPI (Swagger)
*   **Templating:** Thymeleaf
*   **Testing:** JUnit, Mockito

**Architecture:**

The project follows a hexagonal architecture, with the following main components:

*   **Domain:** Contains the core business logic and entities.
*   **Application:** Orchestrates the business logic and exposes it to the outside world through use cases.
*   **Infrastructure:** Implements the technical details, such as database access, REST controllers, and external services.

## Building and Running

**Prerequisites:**

*   Java 17
*   Maven

**Build:**

To build the project, run the following command from the root directory:

```bash
./mvnw clean install
```

**Run:**

To run the application, use the following command:

```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`.

**Database:**

The application uses a MySQL database. You need to configure the database connection in the `src/main/resources/application.properties` file. The application uses Flyway for database migrations, which are located in the `src/main/resources/db/migrations` directory.

## Development Conventions

**Coding Style:**

The project uses the standard Java coding conventions.

**Testing:**

The project uses JUnit and Mockito for testing. The tests are located in the `src/test` directory.

**API Documentation:**

The project uses OpenAPI (Swagger) for API documentation. The documentation is available at `http://localhost:8080/swagger-ui.html`.

**Security:**

The application uses JWT for authentication and has role-based authorization. The security configuration is located in the `src/main/java/com/employed/bar/infrastructure/config/SecurityConfig.java` file.

**User Roles:**

The application has the following user roles:

*   **MANAGER:** Can manage users and perform all actions.
*   **ADMIN:** Can manage users with roles lower than their own.
*   **USER:** Can only access their own data.

**User Management:**

The application provides endpoints for managing users, including creating, deleting, and updating user roles. The user management logic is located in the `src/main/java/com/employed/bar/infrastructure/service/impl/UserManagementServiceImpl.java` file.