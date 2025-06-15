package com.employed.bar.adapters.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApiDocsConfig {

    @Value("${bar.api.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Gestión de Empleados - API")
                        .version("1.0.0")
                        .description("API REST para la gestión integral del personal del bar 'La Esquina Bohemia'")
                        .termsOfService("https://laesquinabohemia.com/terms")
                        .contact(new Contact()
                                .name("Equipo de Soporte")
                                .email("soporte@laesquinabohemia.com")
                                .url("https://laesquinabohemia.com/contact"))
                        .license(new License()
                                .name("Licencia MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url(devUrl)
                                .description("Servidor de Desarrollo")));
    }
}