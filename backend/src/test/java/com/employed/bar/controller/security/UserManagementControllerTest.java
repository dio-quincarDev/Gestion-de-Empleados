package com.employed.bar.controller.security;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.dto.security.request.CreateUserRequest;
import com.employed.bar.infrastructure.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String managerToken;
    private String adminToken;
    private String waiterToken;
    private UserEntity managerUser;
    private UserEntity adminUser;
    private UserEntity waiterUser;

    @BeforeEach
    void setUp() {
        userEntityRepository.deleteAll();

        // Create a MANAGER user
        managerUser = createTestUser("manager@example.com", "password123", EmployeeRole.MANAGER);
        managerToken = generateToken(managerUser.getEmail(), managerUser.getRole().name());

        // Create an ADMIN user
        adminUser = createTestUser("admin@example.com", "password123", EmployeeRole.ADMIN);
        adminToken = generateToken(adminUser.getEmail(), adminUser.getRole().name());

        // Create a WAITER user
        waiterUser = createTestUser("waiter@example.com", "password123", EmployeeRole.WAITER);
        waiterToken = generateToken(waiterUser.getEmail(), waiterUser.getRole().name());
    }

    private UserEntity createTestUser(String email, String password, EmployeeRole role) {
        UserEntity user = UserEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstname("Test")
                .lastname("User")
                .role(role)
                .build();
        return userEntityRepository.save(user);
    }

    private String generateToken(String email, String role) {
        return jwtService.generateToken(email, role).getAccessToken();
    }

    // --- POST /v1/users (createUser) Tests ---

    @Test
    void testCreateUser_Success_AsManager() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("newuser@example.com")
                .password("newpass")
                .firstname("New")
                .lastname("User")
                .role(EmployeeRole.WAITER)
                .build();

        mockMvc.perform(post("/v1/users")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateUser_Failure_AsAdmin() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("admincreatesuser@example.com")
                .password("newpass")
                .firstname("New")
                .lastname("User")
                .role(EmployeeRole.WAITER)
                .build();

        mockMvc.perform(post("/v1/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Only MANAGER can create users
    }

    @Test
    void testCreateUser_Failure_AsWaiter() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("waitercreatesuser@example.com")
                .password("newpass")
                .firstname("New")
                .lastname("User")
                .role(EmployeeRole.WAITER)
                .build();

        mockMvc.perform(post("/v1/users")
                        .header("Authorization", "Bearer " + waiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateUser_Failure_Unauthenticated() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("unauthcreatesuser@example.com")
                .password("newpass")
                .firstname("New")
                .lastname("User")
                .role(EmployeeRole.WAITER)
                .build();

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateUser_Failure_ManagerCreatesManager() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("managermanager@example.com")
                .password("newpass")
                .firstname("New")
                .lastname("Manager")
                .role(EmployeeRole.MANAGER)
                .build();

        mockMvc.perform(post("/v1/users")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // MANAGER cannot create another MANAGER (SecurityException)
    }

    @Test
    void testCreateUser_Failure_InvalidRequestBody() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("invalid") // Invalid email format
                .password("newpass")
                .firstname("New")
                .lastname("User")
                .role(EmployeeRole.WAITER)
                .build();

        mockMvc.perform(post("/v1/users")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void testCreateUser_Failure_EmailAlreadyExists() throws Exception {
        createTestUser("existing@example.com", "pass", EmployeeRole.WAITER);

        CreateUserRequest request = CreateUserRequest.builder()
                .email("existing@example.com")
                .password("newpass")
                .firstname("New")
                .lastname("User")
                .role(EmployeeRole.WAITER)
                .build();

        mockMvc.perform(post("/v1/users")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()); // EmailAlreadyExistException
    }

    // --- DELETE /v1/users/{id} Tests ---

    @Test
    void testDeleteUser_Success_AsManager() throws Exception {
        UserEntity userToDelete = createTestUser("todelete@example.com", "pass", EmployeeRole.WAITER);

        mockMvc.perform(delete("/v1/users/{id}", userToDelete.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_Success_AdminDeletesWaiter() throws Exception {
        UserEntity userToDelete = createTestUser("admindeletes@example.com", "pass", EmployeeRole.WAITER);

        mockMvc.perform(delete("/v1/users/{id}", userToDelete.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_Failure_AsWaiter() throws Exception {
        UserEntity userToDelete = createTestUser("waiterdeletes@example.com", "pass", EmployeeRole.WAITER);

        mockMvc.perform(delete("/v1/users/{id}", userToDelete.getId())
                        .header("Authorization", "Bearer " + waiterToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteUser_Failure_Unauthenticated() throws Exception {
        UserEntity userToDelete = createTestUser("unauthdeletes@example.com", "pass", EmployeeRole.WAITER);

        mockMvc.perform(delete("/v1/users/{id}", userToDelete.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteUser_Failure_UserDeletesSelf() throws Exception {
        mockMvc.perform(delete("/v1/users/{id}", waiterUser.getId())
                        .header("Authorization", "Bearer " + waiterToken))
                .andExpect(status().isForbidden()); // Users cannot delete themselves (SecurityException)
    }

    @Test
    void testDeleteUser_Failure_AdminDeletesManager() throws Exception {
        mockMvc.perform(delete("/v1/users/{id}", managerUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden()); // ADMIN cannot delete MANAGER (SecurityException)
    }

    @Test
    void testDeleteUser_Failure_AdminDeletesAnotherAdmin() throws Exception {
        UserEntity anotherAdmin = createTestUser("anotheradmin@example.com", "pass", EmployeeRole.ADMIN);

        mockMvc.perform(delete("/v1/users/{id}", anotherAdmin.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden()); // ADMIN cannot delete another ADMIN (SecurityException)
    }

    @Test
    void testDeleteUser_Failure_UserNotFound() throws Exception {
        mockMvc.perform(delete("/v1/users/{id}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNotFound()); // UserNotFoundException
    }

    // --- PUT /v1/users/{id}/role Tests ---

    @Test
    void testUpdateUserRole_Success_ManagerChangesWaiterRole() throws Exception {
        UserEntity userToUpdate = createTestUser("waiterupdate@example.com", "pass", EmployeeRole.WAITER);

        mockMvc.perform(put("/v1/users/{id}/role", userToUpdate.getId())
                        .header("Authorization", "Bearer " + managerToken)
                        .param("role", EmployeeRole.CASHIER.name()))
                .andExpect(status().isNoContent());

        // Verify role change
        userEntityRepository.findById(userToUpdate.getId())
                .ifPresent(user -> assertEquals(EmployeeRole.CASHIER, user.getRole()));
    }

    @Test
    void testUpdateUserRole_Success_AdminChangesWaiterRole() throws Exception {
        UserEntity userToUpdate = createTestUser("adminupdate@example.com", "pass", EmployeeRole.WAITER);

        mockMvc.perform(put("/v1/users/{id}/role", userToUpdate.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .param("role", EmployeeRole.CASHIER.name()))
                .andExpect(status().isNoContent());

        // Verify role change
        userEntityRepository.findById(userToUpdate.getId())
                .ifPresent(user -> assertEquals(EmployeeRole.CASHIER, user.getRole()));
    }

    @Test
    void testUpdateUserRole_Failure_AsWaiter() throws Exception {
        UserEntity userToUpdate = createTestUser("waiterupdaterole@example.com", "pass", EmployeeRole.WAITER);

        mockMvc.perform(put("/v1/users/{id}/role", userToUpdate.getId())
                        .header("Authorization", "Bearer " + waiterToken)
                        .param("role", EmployeeRole.CASHIER.name()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateUserRole_Failure_Unauthenticated() throws Exception {
        UserEntity userToUpdate = createTestUser("unauthupdaterole@example.com", "pass", EmployeeRole.WAITER);

        mockMvc.perform(put("/v1/users/{id}/role", userToUpdate.getId())
                        .param("role", EmployeeRole.CASHIER.name()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUserRole_Failure_UserChangesOwnRole() throws Exception {
        mockMvc.perform(put("/v1/users/{id}/role", waiterUser.getId())
                        .header("Authorization", "Bearer " + waiterToken)
                        .param("role", EmployeeRole.CASHIER.name()))
                .andExpect(status().isForbidden()); // Users cannot change their own role (SecurityException)
    }

    @Test
    void testUpdateUserRole_Failure_AdminChangesManagerRole() throws Exception {
        mockMvc.perform(put("/v1/users/{id}/role", managerUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .param("role", EmployeeRole.WAITER.name()))
                .andExpect(status().isForbidden()); // ADMIN cannot change MANAGER role (SecurityException)
    }

    @Test
    void testUpdateUserRole_Failure_AdminChangesAnotherAdminRole() throws Exception {
        UserEntity anotherAdmin = createTestUser("anotheradminforrole@example.com", "pass", EmployeeRole.ADMIN);

        mockMvc.perform(put("/v1/users/{id}/role", anotherAdmin.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .param("role", EmployeeRole.WAITER.name()))
                .andExpect(status().isForbidden()); // ADMIN cannot change another ADMIN role (SecurityException)
    }

    @Test
    void testUpdateUserRole_Failure_AdminPromotesToManager() throws Exception {
        UserEntity userToPromote = createTestUser("promotetomanager@example.com", "pass", EmployeeRole.WAITER);

        mockMvc.perform(put("/v1/users/{id}/role", userToPromote.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .param("role", EmployeeRole.MANAGER.name()))
                .andExpect(status().isForbidden()); // ADMIN cannot promote to MANAGER (SecurityException)
    }

    @Test
    void testUpdateUserRole_Failure_AdminPromotesToAdmin() throws Exception {
        UserEntity userToPromote = createTestUser("promotetoadmin@example.com", "pass", EmployeeRole.WAITER);

        mockMvc.perform(put("/v1/users/{id}/role", userToPromote.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .param("role", EmployeeRole.ADMIN.name()))
                .andExpect(status().isForbidden()); // ADMIN cannot promote to ADMIN (SecurityException)
    }

    @Test
    void testUpdateUserRole_Failure_UserNotFound() throws Exception {
        mockMvc.perform(put("/v1/users/{id}/role", UUID.randomUUID())
                        .header("Authorization", "Bearer " + managerToken)
                        .param("role", EmployeeRole.CASHIER.name()))
                .andExpect(status().isNotFound()); // UserNotFoundException
    }

    @Test
    void testUpdateUserRole_Failure_AssignManagerRoleWhenManagerExists() throws Exception {
        UserEntity userToUpdate = createTestUser("assignmanagerrole@example.com", "pass", EmployeeRole.WAITER);
        // managerUser already exists from setup

        mockMvc.perform(put("/v1/users/{id}/role", userToUpdate.getId())
                        .header("Authorization", "Bearer " + adminToken) // ADMIN can't promote to MANAGER
                        .param("role", EmployeeRole.MANAGER.name()))
                .andExpect(status().isForbidden()); // ADMIN cannot promote to MANAGER (SecurityException)

        // Test with MANAGER trying to assign MANAGER role to another user when one already exists
        mockMvc.perform(put("/v1/users/{id}/role", userToUpdate.getId())
                        .header("Authorization", "Bearer " + managerToken)
                        .param("role", EmployeeRole.MANAGER.name()))
                .andExpect(status().isConflict()); // IllegalStateException
    }

    @Test
    void testUpdateUserRole_Failure_InvalidRoleParam() throws Exception {
        UserEntity userToUpdate = createTestUser("invalidroleparam@example.com", "pass", EmployeeRole.WAITER);

        mockMvc.perform(put("/v1/users/{id}/role", userToUpdate.getId())
                        .header("Authorization", "Bearer " + managerToken)
                        .param("role", "NON_EXISTENT_ROLE"))
                .andExpect(status().isBadRequest()); // ConstraintViolationException or MethodArgumentTypeMismatchException
    }
}
