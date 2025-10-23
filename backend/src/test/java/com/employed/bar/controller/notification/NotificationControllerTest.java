package com.employed.bar.controller.notification;

import com.employed.bar.domain.enums.*;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.ConsumptionEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringAttendanceJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringConsumptionJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.employed.bar.infrastructure.mail.TestMailConfig;
import org.mockito.ArgumentCaptor;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeBodyPart;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMailConfig.class)
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
    private SpringAttendanceJpaRepository springAttendanceJpaRepository;

    @Autowired
    private SpringConsumptionJpaRepository springConsumptionJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String managerJwtToken;
    private String userJwtToken;

    @BeforeEach
    void setUp() {
        userEntityRepository.deleteAll();
        springEmployeeJpaRepository.deleteAll();
        springAttendanceJpaRepository.deleteAll();
        springConsumptionJpaRepository.deleteAll();
        TestMailConfig.clearSentMimeMessages();

        UserEntity managerUser = createTestUser("manager-notify@example.com", "password123", EmployeeRole.MANAGER);
        UserEntity regularUser = createTestUser("user-notify@example.com", "password123", EmployeeRole.WAITER);
        managerJwtToken = generateToken(managerUser.getEmail(), managerUser.getRole().name());
        userJwtToken = generateToken(regularUser.getEmail(), regularUser.getRole().name());
    }

    @Test
    void whenSendTestEmail_asManager_shouldSucceedAndPublishEvent() throws Exception {
        EmployeeEntity employee = createTestEmployee("Test Employee", "employee@example.com", EmployeeRole.WAITER);

        // Create test attendance data for 2025-10-28 with 9 hours difference
        AttendanceRecordEntity attendance = new AttendanceRecordEntity();
        attendance.setEmployee(employee);
        attendance.setEntryDateTime(LocalDateTime.of(2025, 10, 28, 8, 0)); // Changed to 8 AM
        attendance.setExitDateTime(LocalDateTime.of(2025, 10, 28, 17, 0)); // Changed to 5 PM (9 hours)
        attendance.setStatus(com.employed.bar.domain.enums.AttendanceStatus.PRESENT);
        springAttendanceJpaRepository.save(attendance);

        // Create test consumption data for 2025-10-28 with more significant amount
        ConsumptionEntity consumption = new ConsumptionEntity();
        consumption.setEmployee(employee);
        consumption.setConsumptionDate(LocalDateTime.of(2025, 10, 28, 12, 30));
        consumption.setDescription("Test Lunch");
        consumption.setAmount(new java.math.BigDecimal("35.75")); // Increased amount
        springConsumptionJpaRepository.save(consumption);

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .header("Authorization", "Bearer " + managerJwtToken)
                        .param("employeeId", String.valueOf(employee.getId())))
                .andExpect(status().isOk());

        Thread.sleep(2000); // Increased sleep time to ensure async processing completes

        // Verify that an email was sent and check its content
        List<MimeMessage> sentMessages = TestMailConfig.getSentMimeMessages();
        assertEquals(1, sentMessages.size(), "Should have sent exactly one email");

        MimeMessage sentMessage = sentMessages.get(0);
        assertNotNull(sentMessage, "Sent message should not be null");
        assertEquals("Your Weekly Report", sentMessage.getSubject());
        assertNotNull(sentMessage.getContent(), "Email content should not be null");

        String emailContent = extractEmailContent(sentMessage);
        assertFalse(emailContent.isEmpty(), "Email content should not be empty");
        System.out.println("Captured Email Content:\n" + emailContent);

        // Assertions for content - using more flexible contains checks
        assertTrue(emailContent.contains("Test Employee"), "Should contain employee name");
        assertTrue(emailContent.contains("Asistencia") || emailContent.contains("Attendance"), "Should contain attendance section");
        assertTrue(emailContent.contains("Consumo") || emailContent.contains("Consumption"), "Should contain consumption section");
        assertTrue(emailContent.contains("Horas") || emailContent.contains("Hours"), "Should contain hours information");
        assertTrue(emailContent.contains("35.75") || emailContent.contains("35,75"), "Should contain consumption amount");
    }

    private String extractEmailContent(MimeMessage message) throws Exception {
        if (message.getContent() instanceof MimeMultipart) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                MimeBodyPart bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/html")) {
                    return (String) bodyPart.getContent();
                }
            }
        } else if (message.getContent() instanceof String) {
            return (String) message.getContent();
        }
        return "";
    }

    @Test
    void whenSendTestEmail_asUser_shouldReturnForbidden() throws Exception {
        EmployeeEntity employee = createTestEmployee("Forbidden Employee", "forbidden@example.com", EmployeeRole.WAITER);

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .header("Authorization", "Bearer " + userJwtToken)
                        .param("employeeId", String.valueOf(employee.getId())))
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
        Long nonExistentEmployeeId = 999L;

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
        employee.setHourlyRate(new java.math.BigDecimal("15.00")); // Increased rate
        employee.setSalary(new java.math.BigDecimal("2000.00")); // Increased salary
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setPaymentType(PaymentType.HOURLY);
        employee.setPaysOvertime(true);
        employee.setOvertimeRateType(OvertimeRateType.FIFTY_PERCENT);
        employee.setPaymentMethodType(PaymentMethodType.CASH);
        return springEmployeeJpaRepository.save(employee);
    }
}