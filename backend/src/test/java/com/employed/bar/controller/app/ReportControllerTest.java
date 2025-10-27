package com.employed.bar.controller.app;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.report.HoursCalculation;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.domain.port.in.payment.GeneratePaymentUseCase;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.infrastructure.security.jwt.JwtService;
import com.employed.bar.infrastructure.mail.TestMailConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMailConfig.class)
public class ReportControllerTest {

    private static final String BASE_URL = ApiPathConstants.V1_ROUTE + ApiPathConstants.REPORT_ROUTE;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportingUseCase reportingUseCase;

    @MockBean
    private GeneratePaymentUseCase generatePaymentUseCase;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String managerToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userEntityRepository.deleteAll();

        UserEntity managerUser = createTestUser("manager@example.com", "password123", EmployeeRole.MANAGER);
        UserEntity adminUser = createTestUser("admin@example.com", "password123", EmployeeRole.ADMIN);

        managerToken = "Bearer " + jwtService.generateToken(managerUser.getEmail(), managerUser.getRole().name()).getAccessToken();
        adminToken = "Bearer " + jwtService.generateToken(adminUser.getEmail(), adminUser.getRole().name()).getAccessToken();
    }

    @Test
    void getCompleteReport_WithValidDataAndEmployeeId_ShouldReturnReport() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long employeeId = 1L;

        Report mockReport = createMockReport(employeeId);
        when(reportingUseCase.generateCompleteReportForEmployeeById(startDate, endDate, employeeId))
                .thenReturn(mockReport);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/complete")
                        .header("Authorization", managerToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("employeeId", employeeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttendanceHours").value(BigDecimal.valueOf(160.0).doubleValue()))
                .andExpect(jsonPath("$.individualConsumptionReports").isArray());
    }

    @Test
    void getCompleteReport_WithValidDataWithoutEmployeeId_ShouldReturnReport() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long employeeId = 1L;

        Report mockReport = createMockReport(employeeId);
        when(reportingUseCase.generateCompleteReportForEmployeeById(eq(startDate), eq(endDate), any()))
                .thenReturn(mockReport);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/complete")
                        .header("Authorization", managerToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttendanceHours").value(BigDecimal.valueOf(160.0).doubleValue()))
                .andExpect(jsonPath("$.totalConsumptionAmount").value(250.75));
    }


    @Test
    void getCompleteReport_WithMissingStartDate_ShouldReturnBadRequest() throws Exception {
        // Given
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long employeeId = 1L;

        // When & Then
        mockMvc.perform(get(BASE_URL + "/complete")
                        .header("Authorization", managerToken)
                        .param("endDate", endDate.toString())
                        .param("employeeId", employeeId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCompleteReport_WithMissingEndDate_ShouldReturnBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        Long employeeId = 1L;

        // When & Then
        mockMvc.perform(get(BASE_URL + "/complete")
                        .header("Authorization", managerToken)
                        .param("startDate", startDate.toString())
                        .param("employeeId", employeeId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCompleteReport_WithInvalidEmployeeId_ShouldReturnNotFound() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long employeeId = 999L;

        when(reportingUseCase.generateCompleteReportForEmployeeById(startDate, endDate, employeeId))
                .thenThrow(new EmployeeNotFoundException("Employee not found"));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/complete")
                        .header("Authorization", managerToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("employeeId", employeeId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCompleteReport_WithInvalidDateRange_ShouldReturnBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 31);
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        Long employeeId = 1L;

        // When & Then
        mockMvc.perform(get(BASE_URL + "/complete")
                        .header("Authorization", managerToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("employeeId", employeeId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generatePayment_WithValidData_ShouldReturnPayment() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long employeeId = 1L;
        BigDecimal expectedPayment = new BigDecimal("2400.00");

        when(generatePaymentUseCase.generatePayment(employeeId, startDate, endDate))
                .thenReturn(expectedPayment);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/payment")
                        .header("Authorization", managerToken)
                        .param("employeeId", employeeId.toString())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("2400.00"));
    }

    @Test
    void generatePayment_WithMissingStartDate_ShouldReturnBadRequest() throws Exception {
        // Given
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long employeeId = 1L;

        // When & Then
        mockMvc.perform(get(BASE_URL + "/payment")
                        .header("Authorization", managerToken)
                        .param("employeeId", employeeId.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generatePayment_WithMissingEndDate_ShouldReturnBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        Long employeeId = 1L;

        // When & Then
        mockMvc.perform(get(BASE_URL + "/payment")
                        .header("Authorization", managerToken)
                        .param("employeeId", employeeId.toString())
                        .param("startDate", startDate.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generatePayment_WithInvalidEmployeeId_ShouldReturnNotFound() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long employeeId = 999L;

        when(generatePaymentUseCase.generatePayment(employeeId, startDate, endDate))
                .thenThrow(new RuntimeException("Employee not found"));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/payment")
                        .header("Authorization", managerToken)
                        .param("employeeId", employeeId.toString())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCompleteReport_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long employeeId = 1L;

        // When & Then
        mockMvc.perform(get(BASE_URL + "/complete")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("employeeId", employeeId.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCompleteReport_WithUserRoleAdmin_ShouldReturnForbidden() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long employeeId = 1L;

        // No necesitamos mockear el servicio porque la petición será rechazada por seguridad antes

        // When & Then
        mockMvc.perform(get(BASE_URL + "/complete")
                        .header("Authorization", adminToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("employeeId", employeeId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCompleteReport_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long employeeId = 1L;

        when(reportingUseCase.generateCompleteReportForEmployeeById(startDate, endDate, employeeId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/complete")
                        .header("Authorization", managerToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("employeeId", employeeId.toString()))
                .andExpect(status().isInternalServerError());
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

    private Report createMockReport(Long employeeId) {
        // Crear un reporte mock que coincida con la estructura que el mapper convertirá a ReportDto
        return new Report(
                employeeId, // employeeId - aunque no aparece en el DTO, el servicio lo necesita
                java.util.Collections.emptyList(), // attendanceLines (se mapearán a attendanceReports)
                java.util.Collections.emptyList(), // consumptionLines (se mapearán a individualConsumptionReports)
                new HoursCalculation(new BigDecimal("160.0"), new BigDecimal("160.0"), BigDecimal.ZERO), // hoursCalculation
                new BigDecimal("250.75"), // totalConsumption (se mapeará a totalConsumptionAmount)
                new BigDecimal("2400.00") // totalEarnings - aunque no aparece en el DTO
        );
    }
}