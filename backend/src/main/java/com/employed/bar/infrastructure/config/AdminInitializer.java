package com.employed.bar.infrastructure.config;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Verificar si ya existe un MANAGER
        if (userEntityRepository.findByRole(EmployeeRole.MANAGER).isEmpty()) {
            UserEntity managerUser = UserEntity.builder()
                    .email("manager@system.com")
                    .password(passwordEncoder.encode("manager123"))
                    .firstname("System")
                    .lastname("Manager")
                    .role(EmployeeRole.MANAGER) // ← CAMBIAR A MANAGER
                    .build();
            userEntityRepository.save(managerUser);
            System.out.println("✅ MANAGER user created: manager@system.com");
        }

        // Opcional: Crear ADMIN inicial si no existe
        if (userEntityRepository.findByRole(EmployeeRole.ADMIN).isEmpty()) {
            UserEntity adminUser = UserEntity.builder()
                    .email("admin@system.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstname("System")
                    .lastname("Admin")
                    .role(EmployeeRole.ADMIN)
                    .build();
            userEntityRepository.save(adminUser);
            System.out.println("✅ ADMIN user created: admin@system.com");
        }
    }
}