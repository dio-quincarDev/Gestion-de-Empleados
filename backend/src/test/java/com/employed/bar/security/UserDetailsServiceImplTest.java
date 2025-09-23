package com.employed.bar.security;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.security.user.UsersDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @InjectMocks
    private UsersDetailsServiceImpl usersDetailsService;

    @Test
    void loadUserByUsername_UserFound() {
        // Arrange
        String email = "user@test.com";
        UserEntity userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password("password")
                .firstname("Test")
                .lastname("User")
                .role(EmployeeRole.CASHIER)
                .build();
        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // Act
        UserDetails userDetails = usersDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(userEntity.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + EmployeeRole.CASHIER.name())));
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        // Arrange
        String email = "notfound@test.com";
        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            usersDetailsService.loadUserByUsername(email);
        });

        assertEquals("User with email " + email + " not found", exception.getMessage());
    }
}
