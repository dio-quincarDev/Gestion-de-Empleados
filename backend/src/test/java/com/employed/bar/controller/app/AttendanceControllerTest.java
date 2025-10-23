package com.employed.bar.controller.app;

import com.employed.bar.application.service.AttendanceApplicationService;
import com.employed.bar.domain.enums.*;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.exceptions.InvalidAttendanceDataException;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringAttendanceJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.domain.AttendanceDto;
import com.employed.bar.infrastructure.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

    @MockBean
    private AttendanceApplicationService attendanceApplicationService;

    private String managerToken;
    private EmployeeEntity testEmployee;
    private UserEntity managerUser; // Referencia al usuario manager para pruebas de seguridad


    // --- SETUP Y UTILS OPTIMIZADOS ---

    /**
     * Limpieza y preparación antes de cada prueba.
     * Se implementa la generación de UUID para prevenir la violación de unicidad de EMAIL.
     */
    @BeforeEach
    void setUp() {
        // Limpieza de datos en orden inverso para evitar violaciones de FK
        attendanceRepository.deleteAll();
        employeeRepository.deleteAll();
        userEntityRepository.deleteAll();

        // Crear usuario manager con email único para prevenir el error 23505
        managerUser = createTestUser(EmployeeRole.MANAGER);
        managerToken = "Bearer " + jwtService.generateToken(managerUser.getEmail(), managerUser.getRole().name()).getAccessToken();

        // Crear empleado de prueba con datos aleatorios
        testEmployee = createTestEmployee(EmployeeRole.WAITER, EmployeeStatus.ACTIVE);
    }

    /**
     * Genera un UserEntity válido con email único y lo guarda.
     */
    private UserEntity createTestUser(EmployeeRole role) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "manager-test-" + uniqueId + "@bar.com";

        UserEntity user = UserEntity.builder()
                .email(uniqueEmail)
                .password(passwordEncoder.encode("password"))
                // Se asume que has corregido UserEntity con @Column(name = "first_name")
                .firstname("Test")
                .lastname("User")
                .role(role)
                .build();
        return userEntityRepository.save(user);
    }

    /**
     * Genera un EmployeeEntity válido con datos aleatorios y lo guarda.
     */
    private EmployeeEntity createTestEmployee(EmployeeRole role, EmployeeStatus status) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        // Generación de salarios y tarifas aleatorias
        BigDecimal randomHourlyRate = BigDecimal.valueOf(10 + Math.random() * 20).setScale(2, RoundingMode.HALF_UP);
        BigDecimal randomSalary = BigDecimal.valueOf(2000 + Math.random() * 5000).setScale(2, RoundingMode.HALF_UP);

        EmployeeEntity employee = new EmployeeEntity();

        // Datos Base
        employee.setName("Employee " + uniqueId);
        employee.setEmail("test.employee." + uniqueId + "@bar.com");
        employee.setContactPhone("5076" + uniqueId.replaceAll("-", ""));
        employee.setRole(role);
        employee.setStatus(status);

        // Datos de Pago (Asegura NOT NULL)
        employee.setHourlyRate(randomHourlyRate);
        employee.setSalary(randomSalary);
        employee.setPaymentType(PaymentType.HOURLY);
        employee.setPaysOvertime(true);
        employee.setOvertimeRateType(OvertimeRateType.FIFTY_PERCENT);

        // Datos de Método de Pago Aplanado (para satisfacer la entidad V1 original)
        employee.setPaymentMethodType(PaymentMethodType.ACH);
        employee.setBankName("Random Bank");
        employee.setAccountNumber(uniqueId + "12345678");
        employee.setBankAccountType(BankAccount.CHECKING);

        return employeeRepository.save(employee);
    }

    // --- PRUEBAS DE REGISTRO (POST /v1/attendance) ---

    @Test
    void whenRegisterAttendance_shouldSucceed() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(testEmployee.getId());
        attendanceDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 9, 0));
        attendanceDto.setExitDateTime(LocalDateTime.of(2023, 1, 1, 17, 0));
        attendanceDto.setStatus(AttendanceStatus.PRESENT);

        // El mock devuelve un resultado exitoso
        AttendanceRecordClass savedRecord = createMockRecord(testEmployee.getId(), AttendanceStatus.PRESENT, 1L);

        when(attendanceApplicationService.registerAttendance(any(AttendanceRecordClass.class))).thenReturn(savedRecord);

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(testEmployee.getId().intValue())))
                .andExpect(jsonPath("$.status", is(AttendanceStatus.PRESENT.name())));
    }

    @Test
    void whenRegisterAttendance_shouldBeLate() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(testEmployee.getId());
        attendanceDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 9, 30)); // Llegada tarde
        attendanceDto.setExitDateTime(LocalDateTime.of(2023, 1, 1, 17, 0));
        attendanceDto.setStatus(AttendanceStatus.PRESENT); // La entrada DTO es PRESENT

        // El mock simula que el servicio determina que el estado real es LATE
        AttendanceRecordClass savedRecord = createMockRecord(testEmployee.getId(), AttendanceStatus.LATE, 2L);

        when(attendanceApplicationService.registerAttendance(any(AttendanceRecordClass.class))).thenReturn(savedRecord);

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(AttendanceStatus.LATE.name()))); // Verifica la corrección del servicio
    }

    // EDGE CASE: Entrada nula (ya estaba en el original, pero se mantiene)
    @Test
    void whenRegisterAttendance_withNullEmployeeId_shouldReturnBadRequest() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        // EmployeeId es intencionalmente no configurado
        attendanceDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 9, 0));

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isBadRequest());
    }

    // EDGE CASE: Hora de salida anterior a la hora de entrada (ya estaba, se mantiene)
    @Test
    void whenRegisterAttendance_withExitTimeBeforeEntryTime_shouldReturnBadRequest() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(testEmployee.getId());
        attendanceDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 17, 0));
        attendanceDto.setExitDateTime(LocalDateTime.of(2023, 1, 1, 9, 0));
        attendanceDto.setStatus(AttendanceStatus.PRESENT);

        when(attendanceApplicationService.registerAttendance(any(AttendanceRecordClass.class)))
                .thenThrow(new InvalidAttendanceDataException("Exit time cannot be before entry time."));

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isBadRequest());
    }

    // EDGE CASE: Empleado no encontrado (simula una FK fallida o empleado inactivo)
    @Test
    void whenRegisterAttendance_forNonExistentEmployee_shouldReturnNotFound() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(999L); // ID que no existe
        attendanceDto.setEntryDateTime(LocalDateTime.now());
        attendanceDto.setExitDateTime(LocalDateTime.now().plusHours(8)); // Add a valid exitDateTime
        attendanceDto.setStatus(AttendanceStatus.PRESENT);

        when(attendanceApplicationService.registerAttendance(any(AttendanceRecordClass.class)))
                .thenThrow(new EmployeeNotFoundException("Employee not found")); // Simular una excepción genérica

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                // Asume que el controlador mapea excepciones genéricas a 500 o Bad Request si no hay handler específico
                .andExpect(status().isNotFound());
    }

    // --- PRUEBAS DE LISTADO (GET /v1/attendance/list) ---

    @Test
    void whenGetAttendanceList_shouldReturnRecords() throws Exception {
        // Mock de un registro de asistencia
        AttendanceRecordClass record = createMockRecord(testEmployee.getId(), AttendanceStatus.PRESENT, 1L);

        when(attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(record));

        mockMvc.perform(get(BASE_URL + "/list")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("startDate", LocalDate.now().minusDays(2).toString())
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is(AttendanceStatus.PRESENT.name())));
    }

    // EDGE CASE: Rango de fechas inválido (ya estaba, se mantiene)
    @Test
    void whenGetAttendanceList_withInvalidDateRange_shouldReturnBadRequest() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.minusDays(1);

        when(attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new IllegalArgumentException("Invalid date range"));

        mockMvc.perform(get(BASE_URL + "/list")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isBadRequest());
    }

    // EDGE CASE: Rango de fechas en formato incorrecto (ya estaba, se mantiene)
    @Test
    void whenGetAttendanceList_withMalformedDate_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/list")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("startDate", "invalid-date-format")
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isBadRequest());
    }

    // --- PRUEBAS DE CÁLCULO DE PORCENTAJE (GET /v1/attendance/percentage) ---

    @Test
    void whenCalculateAttendancePercentage_shouldReturnPercentage() throws Exception {
        when(attendanceApplicationService.calculateAttendancePercentage(anyLong(), anyInt(), anyInt(), anyInt())).thenReturn(95.5);

        mockMvc.perform(get(BASE_URL + "/percentage")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("year", "2023")
                        .param("month", "10")
                        .param("day", "26"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(95.5)));
    }

    // EDGE CASE: Porcentaje del 0% (todos los días faltó)
    @Test
    void whenCalculateAttendancePercentage_isZero_shouldReturnZero() throws Exception {
        when(attendanceApplicationService.calculateAttendancePercentage(anyLong(), anyInt(), anyInt(), anyInt())).thenReturn(0.0);

        mockMvc.perform(get(BASE_URL + "/percentage")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("year", "2023")
                        .param("month", "10")
                        .param("day", "26"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(0.0)));
    }

    // EDGE CASE: Parámetros numéricos malformados
    @Test
    void whenCalculateAttendancePercentage_withMalformedParams_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/percentage")
                        .header("Authorization", managerToken)
                        .param("employeeId", "abc") // ID no numérico
                        .param("year", "2023"))
                .andExpect(status().isBadRequest());
    }

    // --- PRUEBAS DE AUTORIZACIÓN Y SEGURIDAD ---

    // EDGE CASE: Acceso sin token (ya estaba, se mantiene)
    @Test
    void whenAccessEndpoint_withoutToken_shouldBeUnauthorized() throws Exception {
        mockMvc.perform(get(BASE_URL + "/list")
                        .param("employeeId", "1")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-01-31"))
                .andExpect(status().isUnauthorized());
    }

    // EDGE CASE: Acceso con token de ROL INSUFICIENTE (asumiendo que solo MANAGER puede usar el controller)
    @Test
    void whenAccessEndpoint_withWaiterRole_shouldBeForbidden() throws Exception {
        // 1. Crear usuario con rol de menor jerarquía (WAITER)
        UserEntity waiterUser = createTestUser(EmployeeRole.WAITER);
        String waiterToken = "Bearer " + jwtService.generateToken(waiterUser.getEmail(), waiterUser.getRole().name()).getAccessToken();

        // 2. Intentar registrar asistencia con ese token
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(testEmployee.getId());
        attendanceDto.setEntryDateTime(LocalDateTime.of(2023, 1, 1, 9, 0));

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", waiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                // Se espera FORBIDDEN (403) si la configuración de seguridad bloquea al rol WAITER
                .andExpect(status().isForbidden());
    }

    // --- UTILIDAD MOCK ---
    private AttendanceRecordClass createMockRecord(Long employeeId, AttendanceStatus status, Long recordId) {
        AttendanceRecordClass record = new AttendanceRecordClass();
        record.setId(recordId);
        com.employed.bar.domain.model.structure.EmployeeClass employee = new com.employed.bar.domain.model.structure.EmployeeClass();
        employee.setId(employeeId);
        record.setEmployee(employee);
        record.setEntryDateTime(LocalDateTime.now().minusHours(8));
        record.setExitDateTime(LocalDateTime.now());
        record.setStatus(status);
        return record;
    }
}