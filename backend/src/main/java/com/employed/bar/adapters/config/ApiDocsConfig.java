package com.employed.bar.adapters.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class ApiDocsConfig {
    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
               .select()
               .apis(RequestHandlerSelectors.basePackage("com.employed.bar"))
               .paths(PathSelectors.any())
               .build();
    }
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Gestion de Empleados API")
                .description("API para la gestion de empleados, horarios y consumos")
                .version("1.0.0")
                .build();

    }
}
