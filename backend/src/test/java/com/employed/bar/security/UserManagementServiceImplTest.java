package com.employed.bar.security;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.dto.security.request.CreateUserRequest;
import com.employed.bar.infrastructure.security.user.UserManagementServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceImplTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic;

    @BeforeEach
    void setUp() {
        securityContextHolderMockedStatic = Mockito.mockStatic(SecurityContextHolder.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMockedStatic.close();
    }

    private void mockAuthenticatedUser(UserEntity user) {
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(user.getEmail());
        when(userEntityRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void createUser_AsManager_ShouldCreateAdmin() {
        UserEntity manager = new UserEntity(UUID.randomUUID(), "manager@test.com", "pass", "Manager", "User", EmployeeRole.MANAGER);
        mockAuthenticatedUser(manager);

        CreateUserRequest newUserRequest = new CreateUserRequest("admin@test.com", "password", "Admin", "User", EmployeeRole.ADMIN);
        when(userEntityRepository.findByEmail(newUserRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserEntity savedUser = UserEntity.builder().role(EmployeeRole.ADMIN).build();
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        UserEntity createdUser = userManagementService.createUser(newUserRequest);

        assertNotNull(createdUser);
        assertEquals(EmployeeRole.ADMIN, createdUser.getRole());
        verify(userEntityRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_AsAdmin_ShouldThrowExceptionWhenCreatingManager() {
        UserEntity admin = new UserEntity(UUID.randomUUID(), "admin@test.com", "pass", "Admin", "User", EmployeeRole.ADMIN);
        mockAuthenticatedUser(admin);

        CreateUserRequest newUserRequest = new CreateUserRequest("manager@test.com", "password", "New", "Manager", EmployeeRole.MANAGER);

        SecurityException exception = assertThrows(SecurityException.class, () -> userManagementService.createUser(newUserRequest));

        assertEquals("Cannot create a user with a role equal to or higher than your own.", exception.getMessage());
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void deleteUser_AsManager_ShouldDeleteAdmin() {
        UserEntity manager = new UserEntity(UUID.randomUUID(), "manager@test.com", "pass", "Manager", "User", EmployeeRole.MANAGER);
        mockAuthenticatedUser(manager);

        UserEntity adminToDelete = new UserEntity(UUID.randomUUID(), "admin@test.com", "pass", "Admin", "ToDelete", EmployeeRole.ADMIN);
        when(userEntityRepository.findById(adminToDelete.getId())).thenReturn(Optional.of(adminToDelete));

        userManagementService.deleteUser(adminToDelete.getId());

        verify(userEntityRepository).delete(adminToDelete);
    }

    @Test
    void deleteUser_AsAdmin_ShouldThrowExceptionWhenDeletingManager() {
        UserEntity admin = new UserEntity(UUID.randomUUID(), "admin@test.com", "pass", "Admin", "User", EmployeeRole.ADMIN);
        mockAuthenticatedUser(admin);

        UserEntity managerToDelete = new UserEntity(UUID.randomUUID(), "manager@test.com", "pass", "Manager", "ToDelete", EmployeeRole.MANAGER);
        when(userEntityRepository.findById(managerToDelete.getId())).thenReturn(Optional.of(managerToDelete));

        SecurityException exception = assertThrows(SecurityException.class, () -> userManagementService.deleteUser(managerToDelete.getId()));

        assertEquals("Cannot delete a user with a role equal to or higher than your own.", exception.getMessage());
        verify(userEntityRepository, never()).delete(any(UserEntity.class));
    }

    @Test
    void updateUserRole_AsManager_ShouldPromoteCashierToAdmin() {
        UserEntity manager = new UserEntity(UUID.randomUUID(), "manager@test.com", "pass", "Manager", "User", EmployeeRole.MANAGER);
        mockAuthenticatedUser(manager);

        UserEntity cashierToPromote = new UserEntity(UUID.randomUUID(), "cashier@test.com", "pass", "Cashier", "User", EmployeeRole.CASHIER);
        when(userEntityRepository.findById(cashierToPromote.getId())).thenReturn(Optional.of(cashierToPromote));

        userManagementService.updateUserRole(cashierToPromote.getId(), EmployeeRole.ADMIN);

        assertEquals(EmployeeRole.ADMIN, cashierToPromote.getRole());
        verify(userEntityRepository).save(cashierToPromote);
    }

    @Test
    void updateUserRole_AsAdmin_ShouldThrowExceptionWhenPromotingToManager() {
        UserEntity admin = new UserEntity(UUID.randomUUID(), "admin@test.com", "pass", "Admin", "User", EmployeeRole.ADMIN);
        mockAuthenticatedUser(admin);

        UserEntity userToUpdate = new UserEntity(UUID.randomUUID(), "cashier@test.com", "pass", "Cashier", "User", EmployeeRole.CASHIER);
        when(userEntityRepository.findById(userToUpdate.getId())).thenReturn(Optional.of(userToUpdate));

        SecurityException exception = assertThrows(SecurityException.class, () -> userManagementService.updateUserRole(userToUpdate.getId(), EmployeeRole.MANAGER));

        assertEquals("Cannot assign a role that is equal to or higher than your own.", exception.getMessage());
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }
}
