package com.employed.bar.controller.app;

import com.employed.bar.domain.enums.*;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringAttendanceJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.domain.AttendanceDto;
import com.employed.bar.infrastructure.mail.TestMailConfig;
import com.employed.bar.infrastructure.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMailConfig.class)
public class AttendanceControllerTest {

    private static final String BASE_URL = ApiPathConstants.V1_ROUTE + ApiPathConstants.ATTENDANCE_ROUTE;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringEmployeeJpaRepository employeeRepository;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private SpringAttendanceJpaRepository attendanceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String managerToken;
    private String adminToken;
    private EmployeeEntity testEmployee;

    @BeforeEach
    void setUp() {
        // Limpieza de datos
        attendanceRepository.deleteAll();
        employeeRepository.deleteAll();
        userEntityRepository.deleteAll();

        // Crear usuarios con roles que tienen acceso
        UserEntity managerUser = createTestUser(EmployeeRole.MANAGER);
        UserEntity adminUser = createTestUser(EmployeeRole.ADMIN);

        // SOLUCIÓN: Usar "ROLE_MANAGER" y "ROLE_ADMIN"
        managerToken = "Bearer " + jwtService.generateToken(managerUser.getEmail(), "ROLE_MANAGER").getAccessToken();
        adminToken = "Bearer " + jwtService.generateToken(adminUser.getEmail(), "ROLE_ADMIN").getAccessToken();

        // Crear empleado de prueba
        testEmployee = createTestEmployee();
    }

    private UserEntity createTestUser(EmployeeRole role) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = role.name().toLowerCase() + "-test-" + uniqueId + "@bar.com";

        UserEntity user = UserEntity.builder()
                .email(uniqueEmail)
                .password(passwordEncoder.encode("password"))
                .firstname("Test")
                .lastname("User")
                .role(role)
                .build();
        return userEntityRepository.save(user);
    }

    private EmployeeEntity createTestEmployee() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        EmployeeEntity employee = new EmployeeEntity();
        employee.setName("Employee " + uniqueId);
        employee.setEmail("test.employee." + uniqueId + "@bar.com");
        employee.setContactPhone("5076" + uniqueId.replaceAll("-", ""));
        employee.setRole(EmployeeRole.WAITER);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setHourlyRate(BigDecimal.valueOf(15.0));
        employee.setSalary(BigDecimal.valueOf(2000.0));
        employee.setPaymentType(PaymentType.HOURLY);
        employee.setPaysOvertime(true);
        employee.setOvertimeRateType(OvertimeRateType.FIFTY_PERCENT);
        employee.setPaymentMethodType(PaymentMethodType.ACH);
        employee.setBankName("Test Bank");
        employee.setAccountNumber(uniqueId + "12345678");
        employee.setBankAccountType(BankAccount.CHECKING);

        return employeeRepository.save(employee);
    }

    private AttendanceRecordEntity createTestAttendanceRecord(EmployeeEntity employee, AttendanceStatus status) {
        AttendanceRecordEntity record = new AttendanceRecordEntity();
        record.setEmployee(employee);
        record.setEntryDateTime(LocalDateTime.now().minusHours(8));
        record.setExitDateTime(LocalDateTime.now());
        record.setStatus(status);
        return attendanceRepository.save(record);
    }

    // ===== TESTS DE REGISTRO DE ASISTENCIA =====

    @Test
    void whenRegisterAttendance_withManagerRole_shouldSucceed() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(testEmployee.getId());
        attendanceDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 9, 0));
        attendanceDto.setExitDateTime(LocalDateTime.of(2023, 1, 1, 17, 0));

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(testEmployee.getId().intValue())))
                .andExpect(jsonPath("$.status", is(notNullValue())));
    }

    @Test
    void whenRegisterAttendance_withAdminRole_shouldSucceed() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(testEmployee.getId());
        attendanceDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 9, 0));
        attendanceDto.setExitDateTime(LocalDateTime.of(2023, 1, 1, 17, 0));

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(testEmployee.getId().intValue())))
                .andExpect(jsonPath("$.status", is(notNullValue())));
    }

    @Test
    void whenRegisterAttendance_withoutToken_shouldReturnUnauthorized() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(testEmployee.getId());
        attendanceDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 9, 0));
        attendanceDto.setExitDateTime(LocalDateTime.of(2023, 1, 1, 17, 0));

        mockMvc.perform(post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenRegisterAttendance_withInvalidData_shouldReturnBadRequest() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        // Missing required fields

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenRegisterAttendance_withExitBeforeEntry_shouldReturnBadRequest() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(testEmployee.getId());
        attendanceDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 17, 0));
        attendanceDto.setExitDateTime(LocalDateTime.of(2023, 1, 1, 9, 0)); // Salida antes de entrada

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isBadRequest());
    }

    // ===== TESTS DE LISTADO DE ASISTENCIA =====

    @Test
    void whenGetAttendanceList_withManagerRole_shouldSucceed() throws Exception {
        // Crear registro de asistencia de prueba
        createTestAttendanceRecord(testEmployee, AttendanceStatus.PRESENT);

        mockMvc.perform(get(BASE_URL + "/list")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].status", is(notNullValue())));
    }

    @Test
    void whenGetAttendanceList_withAdminRole_shouldSucceed() throws Exception {
        // Crear registro de asistencia de prueba
        createTestAttendanceRecord(testEmployee, AttendanceStatus.PRESENT);

        mockMvc.perform(get(BASE_URL + "/list")
                        .header("Authorization", adminToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].status", is(notNullValue())));
    }

    @Test
    void whenGetAttendanceList_withNoRecords_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get(BASE_URL + "/list")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("startDate", LocalDate.now().minusMonths(1).toString())
                        .param("endDate", LocalDate.now().minusMonths(1).plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ===== TESTS DE CÁLCULO DE PORCENTAJE =====

    @Test
    void whenCalculateAttendancePercentage_withManagerRole_shouldSucceed() throws Exception {
        // Crear algunos registros de asistencia
        createTestAttendanceRecord(testEmployee, AttendanceStatus.PRESENT);

        mockMvc.perform(get(BASE_URL + "/percentage")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("year", "2023")
                        .param("month", "10")
                        .param("day", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(any(Number.class))));
    }

    @Test
    void whenCalculateAttendancePercentage_withAdminRole_shouldSucceed() throws Exception {
        // Crear algunos registros de asistencia
        createTestAttendanceRecord(testEmployee, AttendanceStatus.PRESENT);

        mockMvc.perform(get(BASE_URL + "/percentage")
                        .header("Authorization", adminToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("year", "2023")
                        .param("month", "10")
                        .param("day", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(any(Number.class))));
    }

    // ===== TESTS DE ACTUALIZACIÓN DE ASISTENCIA =====

    @Test
    void whenUpdateAttendance_withManagerRole_shouldSucceed() throws Exception {
        // Crear registro existente
        AttendanceRecordEntity existingRecord = createTestAttendanceRecord(testEmployee, AttendanceStatus.PRESENT);

        AttendanceDto updateDto = new AttendanceDto();
        updateDto.setEmployeeId(testEmployee.getId());
        updateDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        updateDto.setExitDateTime(LocalDateTime.of(2023, 1, 1, 18, 0));

        mockMvc.perform(put(BASE_URL + "/" + existingRecord.getId())
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(testEmployee.getId().intValue())))
                .andExpect(jsonPath("$.status", is(notNullValue())));
    }

    @Test
    void whenUpdateAttendance_withAdminRole_shouldSucceed() throws Exception {
        // Crear registro existente
        AttendanceRecordEntity existingRecord = createTestAttendanceRecord(testEmployee, AttendanceStatus.PRESENT);

        AttendanceDto updateDto = new AttendanceDto();
        updateDto.setEmployeeId(testEmployee.getId());
        updateDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        updateDto.setExitDateTime(LocalDateTime.of(2023, 1, 1, 18, 0));

        mockMvc.perform(put(BASE_URL + "/" + existingRecord.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(testEmployee.getId().intValue())))
                .andExpect(jsonPath("$.status", is("PRESENT")));
    }

    // ===== TESTS DE ELIMINACIÓN DE ASISTENCIA =====

    @Test
    void whenDeleteAttendance_withManagerRole_shouldSucceed() throws Exception {
        // Crear registro existente
        AttendanceRecordEntity existingRecord = createTestAttendanceRecord(testEmployee, AttendanceStatus.PRESENT);

        mockMvc.perform(delete(BASE_URL + "/" + existingRecord.getId())
                        .header("Authorization", managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteAttendance_withAdminRole_shouldSucceed() throws Exception {
        // Crear registro existente
        AttendanceRecordEntity existingRecord = createTestAttendanceRecord(testEmployee, AttendanceStatus.PRESENT);

        mockMvc.perform(delete(BASE_URL + "/" + existingRecord.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    // ===== TESTS DE HORARIOS NOCTURNOS =====

    @Test
    void whenRegisterOvernightAttendance_shouldCalculateStatusCorrectly() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(testEmployee.getId());
        attendanceDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 22, 0));
        attendanceDto.setExitDateTime(LocalDateTime.of(2023, 1, 2, 6, 0));

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andDo(result -> {
                    System.out.println("=== DEBUG 400 ERROR ===");
                    System.out.println("Status: " + result.getResponse().getStatus());
                    System.out.println("Response Body: " + result.getResponse().getContentAsString());
                    System.out.println("=== END DEBUG ===");
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(notNullValue())));
    }

    // ===== TESTS DE VALIDACIÓN =====

    @Test
    void whenGetAttendanceList_withInvalidDateRange_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/list")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("startDate", "invalid-date")
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCalculateAttendancePercentage_withInvalidParams_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/percentage")
                        .header("Authorization", managerToken)
                        .param("employeeId", "invalid-id")
                        .param("year", "2023"))
                .andExpect(status().isBadRequest());
    }
}