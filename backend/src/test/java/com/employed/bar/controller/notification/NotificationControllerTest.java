package com.employed.bar.controller.notification;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.mail.internet.MimeMessage;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private SpringEmployeeJpaRepository springEmployeeJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String managerJwtToken;
    private String userJwtToken;

    @BeforeEach
    void setUp() {
        userEntityRepository.deleteAll();
        UserEntity managerUser = createTestUser("manager-notify@example.com", "password123", EmployeeRole.MANAGER);
        UserEntity regularUser = createTestUser("user-notify@example.com", "password123", EmployeeRole.WAITER);
        managerJwtToken = generateToken(managerUser.getEmail(), managerUser.getRole().name());
        userJwtToken = generateToken(regularUser.getEmail(), regularUser.getRole().name());

        when(javaMailSender.createMimeMessage()).thenReturn(Mockito.mock(jakarta.mail.internet.MimeMessage.class));
    }

    @Test
    void whenSendTestEmail_asManager_shouldSucceedAndPublishEvent() throws Exception {
        EmployeeEntity employee = createTestEmployee("Test Employee", "employee@example.com", EmployeeRole.WAITER);

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .header("Authorization", "Bearer " + managerJwtToken)
                        .param("employeeId", String.valueOf(employee.getId())))
                .andExpect(status().isOk());

        verify(javaMailSender, timeout(2000)).send(any(MimeMessage.class));
    }

    @Test
    void whenSendTestEmail_asUser_shouldReturnForbidden() throws Exception {
        Long employeeId = 1L;

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .header("Authorization", "Bearer " + userJwtToken)
                        .param("employeeId", String.valueOf(employeeId)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenSendTestEmail_withoutToken_shouldReturnUnauthorized() throws Exception {
        Long employeeId = 1L;

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .param("employeeId", String.valueOf(employeeId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenSendTestEmail_withoutEmployeeId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .header("Authorization", "Bearer " + managerJwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenSendTestEmail_toNonExistentEmployee_shouldReturnNotFound() throws Exception {
        Long nonExistentEmployeeId = 999L; // Assuming this ID does not exist

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .header("Authorization", "Bearer " + managerJwtToken)
                        .param("employeeId", String.valueOf(nonExistentEmployeeId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenSendTestEmail_withInvalidEmployeeIdFormat_shouldReturnBadRequest() throws Exception {
        String invalidEmployeeId = "abc";

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .header("Authorization", "Bearer " + managerJwtToken)
                        .param("employeeId", invalidEmployeeId))
                .andExpect(status().isBadRequest());
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

    private EmployeeEntity createTestEmployee(String name, String email, EmployeeRole role) {
       EmployeeEntity employee = new EmployeeEntity();
        employee.setName(name);
        employee.setEmail(email);
        employee.setRole(role);
        employee.setHourlyRate(new java.math.BigDecimal("10.00"));
        employee.setSalary(new java.math.BigDecimal("1000.00"));
        employee.setStatus(com.employed.bar.domain.enums.EmployeeStatus.ACTIVE);
        return springEmployeeJpaRepository.save(employee);
    }
}