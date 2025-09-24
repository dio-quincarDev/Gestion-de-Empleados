package com.employed.bar.controller.security;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.dto.security.request.CreateUserRequest;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.security.request.LoginRequest;
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
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userEntityRepository.deleteAll();
    }

    private void createTestUser(String email, String password, EmployeeRole role) {
        UserEntity user = UserEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstname("Test")
                .lastname("User")
                .role(role)
                .build();
        userEntityRepository.save(user);
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        createTestUser("test@example.com", "password123", EmployeeRole.ADMIN);
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        ResultActions result = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void testLogin_Failure_InvalidPassword() throws Exception {
        // Given
        createTestUser("test@example.com", "password123", EmployeeRole.ADMIN);
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongpassword");

        // When
        ResultActions result = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void testLogin_Failure_UserNotFound() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "password123");

        // When
        ResultActions result = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterManager_Success() throws Exception {
        // Given
        CreateUserRequest registerRequest = CreateUserRequest.builder()
                .email("newmanager@example.com")
                .password("managerpass")
                .firstname("New")
                .lastname("Manager")
                .role(EmployeeRole.MANAGER)
                .build();

        // When
        ResultActions result = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/register-manager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Then
        result.andExpect(status().isCreated());
        // Verify that the manager user is actually created in the database
        userEntityRepository.findByEmail("newmanager@example.com").orElseThrow();
    }

    @Test
    void testRegisterManager_Failure_ManagerAlreadyExists() throws Exception {
        // Given: Register a manager first
        createTestUser("existingmanager@example.com", "managerpass", EmployeeRole.MANAGER);

        CreateUserRequest registerRequest = CreateUserRequest.builder()
                .email("anothermanager@example.com")
                .password("anotherpass")
                .firstname("Another")
                .lastname("Manager")
                .role(EmployeeRole.MANAGER)
                .build();

        // When: Attempt to register another manager
        ResultActions result = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/register-manager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Then
        result.andExpect(status().isConflict()); // Expect 409 Conflict from IllegalStateException
    }

    @Test
    void testRegisterManager_Failure_InvalidRole() throws Exception {
        // Given
        CreateUserRequest registerRequest = CreateUserRequest.builder()
                .email("invalidrole@example.com")
                .password("invalidpass")
                .firstname("Invalid")
                .lastname("Role")
                .role(EmployeeRole.WAITER) // Attempt to register with a non-MANAGER role
                .build();

        // When
        ResultActions result = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/register-manager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Then
        result.andExpect(status().isBadRequest()); // Expect 400 Bad Request from IllegalArgumentException
    }

    @Test
    void testLogin_Failure_InvalidRequestBody() throws Exception {
        // Given: LoginRequest with missing email (invalid)
        LoginRequest invalidLoginRequest = new LoginRequest(null, "password123");

        // When
        ResultActions result = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginRequest)));

        // Then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists()); // Expect validation error for email field
    }

    @Test
    void testLogin_Failure_AccessDeniedForUserRole() throws Exception {
        // Given: A user with EmployeeRole.WAITER
        createTestUser("waiter@example.com", "password123", EmployeeRole.WAITER);
        LoginRequest loginRequest = new LoginRequest("waiter@example.com", "password123");

        // When: Attempt to log in with a WAITER role
        ResultActions result = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Then
        result.andExpect(status().isForbidden()); // Expect 403 Forbidden from AccessDeniedException
    }
}