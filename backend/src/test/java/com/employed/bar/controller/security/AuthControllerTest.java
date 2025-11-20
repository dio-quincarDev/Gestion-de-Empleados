package com.employed.bar.controller.security;

import com.employed.bar.application.notification.NotificationApplicationService;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Base64;

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

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private NotificationApplicationService notificationService;

    @BeforeEach
    void setUp() {
        userEntityRepository.deleteAll();
    }

    private void createTestUser(String email, String password, EmployeeRole role) {
        UserEntity user = UserEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name("Test User")
                .role(role)
                .build();
        userEntityRepository.save(user);
    }

    private void debugJwtToken(String token) throws Exception {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        System.out.println("=== JWT DEBUG ===");
        System.out.println("Header: " + header);
        System.out.println("Payload: " + payload);
        System.out.println("=================");
    }

    @Test
    void login_admin_success() throws Exception {
        // Given
        createTestUser("admin@example.com", "password123", EmployeeRole.ADMIN);
        LoginRequest loginRequest = new LoginRequest("admin@example.com", "password123");

        // When
        ResultActions result = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Then
        String responseContent = result.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.accessToken").isString())
                .andReturn().getResponse().getContentAsString();

        // Extraer y verificar el rol del JWT
        String token = objectMapper.readTree(responseContent).get("accessToken").asText();
        debugJwtToken(token);

        // Verificar que el token contiene el rol correcto
        String[] chunks = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
        assert payload.contains("ROLE_ADMIN") : "JWT no contiene ROLE_ADMIN. Payload: " + payload;
    }

    @Test
    void login_manager_success() throws Exception {
        // Given
        createTestUser("manager@example.com", "password123", EmployeeRole.MANAGER);
        LoginRequest loginRequest = new LoginRequest("manager@example.com", "password123");

        // When
        ResultActions result = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Then
        String responseContent = result.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.accessToken").isString())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(responseContent).get("accessToken").asText();

        // Verificar el rol dentro del JWT
        String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]));
        assert payload.contains("ROLE_MANAGER") : "JWT no contiene ROLE_MANAGER";

        System.out.println("✅ MANAGER role verified in JWT: " + payload);
    }

    @Test
    void register_manager_success() throws Exception {
        // Given
        CreateUserRequest registerRequest = CreateUserRequest.builder()
                .email("manager@example.com")
                .password("managerpass123")
                .name("System Manager")
                .role(EmployeeRole.MANAGER)
                .build();

        // When
        ResultActions result = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/register-manager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Then
        result.andExpect(status().isCreated());

        // Verificar que el usuario se creó con el rol correcto
        UserEntity savedUser = userEntityRepository.findByEmail("manager@example.com").orElseThrow();
        assert savedUser.getRole() == EmployeeRole.MANAGER;
    }
    @Test
    void register_and_login_manager_success() throws Exception {
        // Given - Registrar manager
        CreateUserRequest registerRequest = CreateUserRequest.builder()
                .email("newmanager@example.com")
                .password("newmanager123")
                .name("New Manager")
                .role(EmployeeRole.MANAGER)
                .build();

        // When - Registrar
        mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/register-manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // When - Login
        LoginRequest loginRequest = new LoginRequest("newmanager@example.com", "newmanager123");
        ResultActions loginResult = mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Then - Verificar JWT contiene ROLE_MANAGER
        String token = loginResult.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String payload = new String(Base64.getUrlDecoder().decode(
                objectMapper.readTree(token).get("accessToken").asText().split("\\.")[1]
        ));

        assert payload.contains("ROLE_MANAGER") : "JWT del manager registrado no contiene ROLE_MANAGER";
        System.out.println("✅ Manager registrado y autenticado correctamente con ROLE_MANAGER");
    }
}