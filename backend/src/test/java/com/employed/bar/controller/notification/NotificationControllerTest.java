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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.employed.bar.infrastructure.mail.TestMailConfig;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeBodyPart;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    private static final AtomicInteger testCounter = new AtomicInteger(0);

    @BeforeEach
    void setUp() {
        int testNumber = testCounter.incrementAndGet();
        System.out.println("=== SETUP PARA PRUEBA #" + testNumber + " ===");

        // ORDEN CRÍTICO: Primero limpiar entidades con dependencias
        springConsumptionJpaRepository.deleteAll();
        springAttendanceJpaRepository.deleteAll();
        springEmployeeJpaRepository.deleteAll();
        userEntityRepository.deleteAll();
        TestMailConfig.clearSentMimeMessages();

        // Crear usuarios con emails únicos para cada prueba
        UserEntity managerUser = createTestUser(
                "manager-notify-" + testNumber + "@example.com",
                "password123",
                EmployeeRole.MANAGER
        );
        UserEntity regularUser = createTestUser(
                "user-notify-" + testNumber + "@example.com",
                "password123",
                EmployeeRole.WAITER
        );
        managerJwtToken = generateToken(managerUser.getEmail(), managerUser.getRole().name());
        userJwtToken = generateToken(regularUser.getEmail(), regularUser.getRole().name());

        System.out.println("✅ Setup completado para prueba #" + testNumber);
    }

    @AfterEach
    void tearDown() {
        // Limpieza adicional para asegurar independencia
        springConsumptionJpaRepository.deleteAll();
        springAttendanceJpaRepository.deleteAll();
        springEmployeeJpaRepository.deleteAll();
        userEntityRepository.deleteAll();
        TestMailConfig.clearSentMimeMessages();
        System.out.println("✅ Cleanup completado");
    }

    @Test
    void whenSendTestEmail_asManager_shouldSucceedAndPublishEvent() throws Exception {
        EmployeeEntity employee = createTestEmployee(
                "Test Employee 1",
                "employee1@example.com",
                EmployeeRole.WAITER
        );

        // Define the last activity date for the test
        LocalDateTime lastActivityDateTime = LocalDateTime.of(2025, 10, 28, 17, 0);
        LocalDate lastActivityDate = lastActivityDateTime.toLocalDate();

        // Create test attendance data for a 7-day period ending on lastActivityDate
        // Day 1: 2025-10-22 (8 hours)
        AttendanceRecordEntity attendance1 = new AttendanceRecordEntity();
        attendance1.setEmployee(employee);
        attendance1.setEntryDateTime(LocalDateTime.of(2025, 10, 22, 8, 0));
        attendance1.setExitDateTime(LocalDateTime.of(2025, 10, 22, 16, 0));
        attendance1.setStatus(AttendanceStatus.PRESENT);
        springAttendanceJpaRepository.save(attendance1);

        // Day 2: 2025-10-25 (9 hours)
        AttendanceRecordEntity attendance2 = new AttendanceRecordEntity();
        attendance2.setEmployee(employee);
        attendance2.setEntryDateTime(LocalDateTime.of(2025, 10, 25, 9, 0));
        attendance2.setExitDateTime(LocalDateTime.of(2025, 10, 25, 18, 0));
        attendance2.setStatus(AttendanceStatus.PRESENT);
        springAttendanceJpaRepository.save(attendance2);

        // Day 3: 2025-10-28 (last activity day, 9 hours)
        AttendanceRecordEntity attendance3 = new AttendanceRecordEntity();
        attendance3.setEmployee(employee);
        attendance3.setEntryDateTime(LocalDateTime.of(2025, 10, 28, 8, 0));
        attendance3.setExitDateTime(LocalDateTime.of(2025, 10, 28, 17, 0));
        attendance3.setStatus(AttendanceStatus.PRESENT);
        springAttendanceJpaRepository.save(attendance3);

        // Create test consumption data for the same period
        // Day 1: 2025-10-22 (10.00)
        ConsumptionEntity consumption1 = new ConsumptionEntity();
        consumption1.setEmployee(employee);
        consumption1.setConsumptionDate(LocalDateTime.of(2025, 10, 22, 13, 0));
        consumption1.setDescription("Coffee");
        consumption1.setAmount(new java.math.BigDecimal("10.00"));
        springConsumptionJpaRepository.save(consumption1);

        // Day 2: 2025-10-25 (25.50)
        ConsumptionEntity consumption2 = new ConsumptionEntity();
        consumption2.setEmployee(employee);
        consumption2.setConsumptionDate(LocalDateTime.of(2025, 10, 25, 12, 30));
        consumption2.setDescription("Lunch");
        consumption2.setAmount(new java.math.BigDecimal("25.50"));
        springConsumptionJpaRepository.save(consumption2);

        // Day 3: 2025-10-28 (35.75)
        ConsumptionEntity consumption3 = new ConsumptionEntity();
        consumption3.setEmployee(employee);
        consumption3.setConsumptionDate(LocalDateTime.of(2025, 10, 28, 12, 30));
        consumption3.setDescription("Dinner");
        consumption3.setAmount(new java.math.BigDecimal("35.75"));
        springConsumptionJpaRepository.save(consumption3);

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .header("Authorization", "Bearer " + managerJwtToken)
                        .param("employeeId", String.valueOf(employee.getId())))
                .andExpect(status().isOk());

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

        // Assertions for content - now aggregated over the week
        assertTrue(emailContent.contains("Test Employee 1"), "Should contain employee name");
        assertTrue(emailContent.contains("Asistencia") || emailContent.contains("Attendance"), "Should contain attendance section");
        assertTrue(emailContent.contains("Consumo") || emailContent.contains("Consumption"), "Should contain consumption section");
        assertTrue(emailContent.contains("Horas") || emailContent.contains("Hours"), "Should contain hours information");

        // Total hours: 8 (22/10) + 9 (25/10) + 9 (28/10) = 26 hours
        assertTrue(emailContent.contains("26.0"), "Should contain total hours for the week");

        // Total consumption: 10.00 (22/10) + 25.50 (25/10) + 35.75 (28/10) = 71.25
        assertTrue(emailContent.contains("71.25") || emailContent.contains("71,25"), "Should contain total consumption for the week");
    }

    private String extractEmailContent(MimeMessage message) throws Exception {
        if (message == null) {
            return "";
        }

        Object content = message.getContent();
        if (content instanceof MimeMultipart) {
            MimeMultipart mimeMultipart = (MimeMultipart) content;
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                MimeBodyPart bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/html")) {
                    return (String) bodyPart.getContent();
                }
            }
        } else if (content instanceof String) {
            return (String) content;
        }
        return "";
    }

    @Test
    void whenSendTestEmail_asUser_shouldReturnForbidden() throws Exception {
        EmployeeEntity employee = createTestEmployee(
                "Forbidden Employee 2",
                "forbidden2@example.com",
                EmployeeRole.WAITER
        );

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .header("Authorization", "Bearer " + userJwtToken)
                        .param("employeeId", String.valueOf(employee.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenSendTestEmail_withoutToken_shouldReturnUnauthorized() throws Exception {
        EmployeeEntity employee = createTestEmployee(
                "Unauthorized Employee 3",
                "unauthorized3@example.com",
                EmployeeRole.WAITER
        );

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .param("employeeId", String.valueOf(employee.getId())))
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
        Long nonExistentEmployeeId = 999999L; // ID muy alto que no existe

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE + "/send-test")
                        .header("Authorization", "Bearer " + managerJwtToken)
                        .param("employeeId", String.valueOf(nonExistentEmployeeId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee not found"));
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
        employee.setHourlyRate(new java.math.BigDecimal("15.00"));
        employee.setSalary(new java.math.BigDecimal("2000.00"));
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setPaymentType(PaymentType.HOURLY);
        employee.setPaysOvertime(true);
        employee.setOvertimeRateType(OvertimeRateType.FIFTY_PERCENT);
        employee.setPaymentMethodType(PaymentMethodType.CASH);
        return springEmployeeJpaRepository.save(employee);
    }
}