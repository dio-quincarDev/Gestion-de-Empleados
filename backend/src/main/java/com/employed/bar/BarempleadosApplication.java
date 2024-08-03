package com.employed.bar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.employed.bar")
public class BarempleadosApplication {

    public static void main(String[] args) {
        SpringApplication.run(BarempleadosApplication.class, args);
    }

}
