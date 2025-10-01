package com.employed.bar.security;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AdminInitializerTest {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Test
    void onApplicationStart_shouldCreateDefaultManagerAndAdmin() {
        // The CommandLineRunner (AdminInitializer) runs when the context is loaded.
        // We just need to verify the result.

        // Verify MANAGER exists
        Optional<UserEntity> managerOptional = userEntityRepository.findByEmail("manager@example.com");
        assertTrue(managerOptional.isPresent(), "Default MANAGER should have been created");
        UserEntity manager = managerOptional.get();
        assertEquals("manager@example.com", manager.getEmail());
        assertEquals(EmployeeRole.MANAGER, manager.getRole());

        // Verify ADMIN exists
        Optional<UserEntity> adminOptional = userEntityRepository.findByEmail("admin@example.com");
        assertTrue(adminOptional.isPresent(), "Default ADMIN should have been created");
        UserEntity admin = adminOptional.get();
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals(EmployeeRole.ADMIN, admin.getRole());
    }
}
