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
        if (userEntityRepository.findByRole(EmployeeRole.MANAGER).isEmpty()) {
            UserEntity managerUser = UserEntity.builder()
                    .email("manager@example.com")
                    .password(passwordEncoder.encode("managerpassword"))
                    .firstname("Manager")
                    .lastname("User")
                    .role(EmployeeRole.MANAGER)
                    .build();
            userEntityRepository.save(managerUser);
        }

        if (userEntityRepository.findByRole(EmployeeRole.ADMIN).isEmpty()) {
            UserEntity adminUser = UserEntity.builder()
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("adminpassword"))
                    .firstname("Admin")
                    .lastname("User")
                    .role(EmployeeRole.ADMIN)
                    .build();
            userEntityRepository.save(adminUser);
        }
    }
}
