package com.employed.bar.adapters.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocsConfig {
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestion de Empleados Api")
                        .description("Gestion de Horarios y Consumos del Personal")
                        .version("1.0.0"));

    }
}