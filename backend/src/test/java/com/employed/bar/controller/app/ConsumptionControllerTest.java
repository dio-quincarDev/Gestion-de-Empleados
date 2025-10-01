package com.employed.bar.controller.app;

import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.ConsumptionEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringConsumptionJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.domain.ConsumptionDto;
import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.infrastructure.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ConsumptionControllerTest {

    private static final String BASE_URL = ApiPathConstants.V1_ROUTE + ApiPathConstants.CONSUMPTION_ROUTE;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SpringConsumptionJpaRepository consumptionRepository;

    @Autowired
    private SpringEmployeeJpaRepository employeeRepository;

    private EmployeeEntity testEmployee;
    private UserEntity managerUser;
    private String managerToken;

    @BeforeEach
    void setUp() {
        // Configurar ObjectMapper para manejar LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        // Limpiar base de datos antes de cada test
        consumptionRepository.deleteAll();
        employeeRepository.deleteAll();
        userEntityRepository.deleteAll();

        // Crear empleado y usuario de prueba usando los métodos helper
        testEmployee = createTestEmployee("Juan Pérez", "juan.perez@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);
        managerUser = createTestUser("manager-consumption@test.com", "password", EmployeeRole.MANAGER);
        managerToken = "Bearer " + jwtService.generateToken(managerUser.getEmail(), managerUser.getRole().name()).getAccessToken();
    }

    // Métodos helper para crear entidades de prueba
    private EmployeeEntity createTestEmployee(String name, String email, EmployeeRole role, EmployeeStatus status) {
        EmployeeEntity employee = EmployeeEntity.builder()
                .name(name)
                .email(email)
                .role(role)
                .hourlyRate(new BigDecimal("10.00"))
                .salary(new BigDecimal("1500.00"))
                .status(status)
                .build();
        return employeeRepository.save(employee);
    }

    private UserEntity createTestUser(String email, String password, EmployeeRole role) {
        UserEntity user = UserEntity.builder()
                .firstname("Test")
                .lastname("User")
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        return userEntityRepository.save(user);
    }

    @Test
    void createConsumption_WithValidData_ShouldReturnCreatedConsumption() throws Exception {
        // Given
        ConsumptionDto consumptionDto = ConsumptionDto.builder()
                .employeeId(testEmployee.getId())
                .amount(new BigDecimal("25.50"))
                .description("Almuerzo")
                .date(LocalDateTime.now())
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consumptionDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(25.50)))
                .andExpect(jsonPath("$.description", is("Almuerzo")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void createConsumption_WithInvalidEmployee_ShouldReturnNotFound() throws Exception {
        // Given
        ConsumptionDto consumptionDto = ConsumptionDto.builder()
                .employeeId(999L) // ID que no existe
                .amount(new BigDecimal("25.50"))
                .description("Almuerzo")
                .date(LocalDateTime.now())
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consumptionDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void createConsumption_WithNegativeAmount_ShouldReturnBadRequest() throws Exception {
        // Given - Monto negativo (viola @Positive)
        ConsumptionDto consumptionDto = ConsumptionDto.builder()
                .employeeId(testEmployee.getId())
                .amount(new BigDecimal("-10.00")) // Monto negativo
                .description("Almuerzo")
                .date(LocalDateTime.now())
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consumptionDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount", notNullValue()));
    }

    @Test
    void createConsumption_WithNullAmount_ShouldReturnBadRequest() throws Exception {
        // Given
        ConsumptionDto consumptionDto = ConsumptionDto.builder()
                .employeeId(testEmployee.getId())
                .amount(null) // Amount nulo
                .description("Almuerzo")
                .date(LocalDateTime.now())
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consumptionDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount", notNullValue()));
    }

    @Test
    void createConsumption_WithNullEmployeeId_ShouldReturnBadRequest() throws Exception {
        // Given
        ConsumptionDto consumptionDto = ConsumptionDto.builder()
                .employeeId(null) // EmployeeId nulo
                .amount(new BigDecimal("25.50"))
                .description("Almuerzo")
                .date(LocalDateTime.now())
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consumptionDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.employeeId", notNullValue()));
    }

    @Test
    void createConsumption_WithLongDescription_ShouldReturnBadRequest() throws Exception {
        // Given - Descripción muy larga
        String longDescription = "A".repeat(256); // Excede los 255 caracteres
        ConsumptionDto consumptionDto = ConsumptionDto.builder()
                .employeeId(testEmployee.getId())
                .amount(new BigDecimal("25.50"))
                .description(longDescription)
                .date(LocalDateTime.now())
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consumptionDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", notNullValue()));
    }

    @Test
    void createConsumption_WithNullDate_ShouldReturnBadRequest() throws Exception {
        // Given
        ConsumptionDto consumptionDto = ConsumptionDto.builder()
                .employeeId(testEmployee.getId())
                .amount(new BigDecimal("25.50"))
                .description("Almuerzo")
                .date(null) // Fecha nula
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consumptionDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.date", notNullValue()));
    }

    @Test
    void getTotalConsumptionByEmployee_WithValidData_ShouldReturnTotal() throws Exception {
        // Given - Crear consumos de prueba
        createTestConsumptions();

        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/total")
                .header("Authorization", managerToken)
                .param("employeeId", testEmployee.getId().toString())
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()));

        // Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()));
    }

    @Test
    void getTotalConsumptionByEmployee_WithNonExistentEmployee_ShouldReturnNotFound() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/total")
                .header("Authorization", managerToken)
                .param("employeeId", "999")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getTotalConsumptionByEmployee_WithInvalidDateRange_ShouldReturnBadRequest() throws Exception {
        // Given - Fecha inicio mayor que fecha fin
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(7);

        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/total")
                .header("Authorization", managerToken)
                .param("employeeId", testEmployee.getId().toString())
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTotalConsumptionForAllEmployees_WithValidData_ShouldReturnTotal() throws Exception {
        // Given - Crear consumos de prueba
        createTestConsumptions();

        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/total/all")
                .header("Authorization", managerToken)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()));

        // Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()));
    }

    @Test
    void getTotalConsumptionForAllEmployees_WithNoConsumptions_ShouldReturnZero() throws Exception {
        // Given - No hay consumos creados
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/total/all")
                .header("Authorization", managerToken)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()));

        // Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void createConsumption_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Given
        ConsumptionDto consumptionDto = ConsumptionDto.builder()
                .employeeId(testEmployee.getId())
                .amount(new BigDecimal("25.50"))
                .description("Almuerzo")
                .date(LocalDateTime.now())
                .build();

        // When - Sin header de Authorization
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consumptionDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTotalConsumptionByEmployee_WithUserRoleWaiter_ShouldReturnForbidden() throws Exception {
        // Given - Crear usuario con rol WAITER (no tiene acceso)
        UserEntity waiterUser = createTestUser("waiter-consumption@test.com", "password", EmployeeRole.WAITER);
        String waiterToken = "Bearer " + jwtService.generateToken(waiterUser.getEmail(), waiterUser.getRole().name()).getAccessToken();

        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/total")
                .header("Authorization", waiterToken)
                .param("employeeId", testEmployee.getId().toString())
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()));

        // Then
        response.andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createConsumption_WithUserRoleAdmin_ShouldReturnSuccess() throws Exception {
        // Given - Crear usuario con rol ADMIN (debe tener acceso)
        UserEntity adminUser = createTestUser("admin-consumption@test.com", "password", EmployeeRole.ADMIN);
        String adminToken = "Bearer " + jwtService.generateToken(adminUser.getEmail(), adminUser.getRole().name()).getAccessToken();

        ConsumptionDto consumptionDto = ConsumptionDto.builder()
                .employeeId(testEmployee.getId())
                .amount(new BigDecimal("30.00"))
                .description("Cena")
                .date(LocalDateTime.now())
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consumptionDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(30.00)))
                .andExpect(jsonPath("$.description", is("Cena")));
    }

    private void createTestConsumptions() {
        // Crear varios consumos de prueba
        ConsumptionEntity consumption1 = ConsumptionEntity.builder()
                .employee(testEmployee)
                .amount(new BigDecimal("25.50"))
                .description("Almuerzo")
                .consumptionDate(LocalDateTime.now().minusDays(1))
                .build();

        ConsumptionEntity consumption2;
        consumption2 = ConsumptionEntity.builder()
                .employee(testEmployee)
                .amount(new BigDecimal("15.75"))
                .description("Cena")
                .consumptionDate(LocalDateTime.now().minusDays(3))
                .build();

        consumptionRepository.save(consumption1);
        consumptionRepository.save(consumption2);
    }
}