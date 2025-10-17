package com.employed.bar.controller.kpi;

import com.employed.bar.domain.enums.AttendanceStatus;
import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.enums.PaymentMethodType;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.ConsumptionClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.ConsumptionRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class KpiControllerTest {

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

    // Repositories for KPI data
    private final SpringEmployeeJpaRepository employeeRepository;
    private final SpringAttendanceJpaRepository attendanceRepository;
    private final SpringConsumptionJpaRepository consumptionRepository;

    private String managerToken;
    private String adminToken;
    private String waiterToken;
    private UserEntity managerUser;
    private UserEntity adminUser;
    private UserEntity waiterUser;

    @Autowired
    public KpiControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, UserEntityRepository userEntityRepository, PasswordEncoder passwordEncoder, JwtService jwtService, com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository employeeRepository, com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringAttendanceJpaRepository attendanceRepository, com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringConsumptionJpaRepository consumptionRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.consumptionRepository = consumptionRepository;
    }

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
        consumptionRepository.deleteAll();
        employeeRepository.deleteAll();
        userEntityRepository.deleteAll();

        // Create users
        managerUser = createTestUser("manager@example.com", "password123", EmployeeRole.MANAGER);
        managerToken = generateToken(managerUser.getEmail(), managerUser.getRole().name());

        adminUser = createTestUser("admin@example.com", "password123", EmployeeRole.ADMIN);
        adminToken = generateToken(adminUser.getEmail(), adminUser.getRole().name());

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

    private EmployeeEntity createEmployee(String name, EmployeeRole role, EmployeeStatus status) {
        EmployeeEntity employee = new EmployeeEntity();
        employee.setName(name);
        employee.setRole(role);
        employee.setStatus(status);
        employee.setHourlyRate(BigDecimal.valueOf(10.0)); // Default hourly rate
        employee.setEmail(name.toLowerCase().replace(" ", "") + "@example.com"); // Generate a unique email
        employee.setPaysOvertime(false);
        employee.setPaymentMethodType(PaymentMethodType.CASH);
        return employeeRepository.save(employee);
    }

    private AttendanceRecordEntity createAttendance(EmployeeEntity employee, LocalDateTime entry, LocalDateTime exit) {
        AttendanceRecordEntity attendance = new AttendanceRecordEntity();
        attendance.setEmployee(employee);
        attendance.setDate(entry.toLocalDate());
        attendance.setEntryTime(entry.toLocalTime());
        attendance.setExitTime(exit.toLocalTime());
        attendance.setStatus(AttendanceStatus.PRESENT); // Default status
        return attendanceRepository.save(attendance);
    }

    private ConsumptionEntity createConsumption(EmployeeEntity employee, BigDecimal amount, LocalDateTime dateTime) {
        ConsumptionEntity consumption = new ConsumptionEntity();
        consumption.setEmployee(employee);
        consumption.setAmount(amount);
        consumption.setConsumptionDate(dateTime);
        consumption.setDescription("Test Consumption"); // Default description
        return consumptionRepository.save(consumption);
    }

    // --- GET /v1/kpis/manager Tests ---

    @Test
    void testGetManagerKpis_Failure_Unauthenticated() throws Exception {
        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.KPI_ROUTE + "/manager")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-01-31"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetManagerKpis_Failure_AsWaiter() throws Exception {
        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.KPI_ROUTE + "/manager")
                        .header("Authorization", "Bearer " + waiterToken)
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-01-31"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetManagerKpis_Success_AsAdmin() throws Exception {
        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.KPI_ROUTE + "/manager")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActiveEmployees").isNumber());
    }

    @Test
    void testGetManagerKpis_Success_AsManager() throws Exception {
        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.KPI_ROUTE + "/manager")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActiveEmployees").isNumber());
    }

  /*  @Test
    void testGetManagerKpis_Success_WithData() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        EmployeeEntity activeWaiter = createEmployee("Active Waiter", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);
        EmployeeEntity inactiveChef = createEmployee("Inactive Chef", EmployeeRole.CHEF, EmployeeStatus.INACTIVE);

        createAttendance(activeWaiter, LocalDateTime.of(2023, 1, 15, 9, 0), LocalDateTime.of(2023, 1, 15, 17, 0)); // 8 hours
        createConsumption(activeWaiter, BigDecimal.valueOf(50.0), LocalDateTime.of(2023, 1, 15, 12, 0));

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.KPI_ROUTE + "/manager")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActiveEmployees").value(1))
                .andExpect(jsonPath("$.totalInactiveEmployees").value(1))
                .andExpect(jsonPath("$.totalHoursWorkedOverall").value(8.0))
                .andExpect(jsonPath("$.totalConsumptionsOverall").value(50.0))
                .andExpect(jsonPath("$.topEmployeesByHoursWorked[0].employeeName").value("Active Waiter"))
                .andExpect(jsonPath("$.topEmployeesByConsumptions[0].employeeName").value("Active Waiter"));
    }*/

    @Test
    void testGetManagerKpis_Success_NoDataFound() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.KPI_ROUTE + "/manager")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActiveEmployees").value(0))
                .andExpect(jsonPath("$.totalInactiveEmployees").value(0))
                .andExpect(jsonPath("$.totalHoursWorkedOverall").value(0.0))
                .andExpect(jsonPath("$.totalConsumptionsOverall").value(0.0))
                .andExpect(jsonPath("$.topEmployeesByHoursWorked").isEmpty())
                .andExpect(jsonPath("$.topEmployeesByConsumptions").isEmpty());
    }

    @Test
    void testGetManagerKpis_Failure_MissingStartDate() throws Exception {
        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.KPI_ROUTE + "/manager")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("endDate", "2023-01-31"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }

    @Test
    void testGetManagerKpis_Failure_MissingEndDate() throws Exception {
        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.KPI_ROUTE + "/manager")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("startDate", "2023-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }

    @Test
    void testGetManagerKpis_Failure_InvalidDateFormat() throws Exception {
        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.KPI_ROUTE + "/manager")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("startDate", "invalid-date")
                        .param("endDate", "2023-01-31"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }

    @Test
    void testGetManagerKpis_Failure_StartDateAfterEndDate() throws Exception {
        // Service does not throw an exception for this, it returns empty/zero KPIs
        LocalDate startDate = LocalDate.of(2023, 1, 31);
        LocalDate endDate = LocalDate.of(2023, 1, 1);

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.KPI_ROUTE + "/manager")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActiveEmployees").value(0))
                .andExpect(jsonPath("$.totalInactiveEmployees").value(0))
                .andExpect(jsonPath("$.totalHoursWorkedOverall").value(0.0))
                .andExpect(jsonPath("$.totalConsumptionsOverall").value(0.0));
    }
}