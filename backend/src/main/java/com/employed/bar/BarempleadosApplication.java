package com.employed.bar;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;




@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.employed.bar")
@OpenAPIDefinition(
        info = @Info(
                title = "Gestion de Empleados API",
                version = "1.0",
                description = "API para la gestión de empleados, horarios y consumos"
        )
)
public class BarempleadosApplication {

    public static void main(String[] args) {
        SpringApplication.run(BarempleadosApplication.class, args);
    }

}
