package com.employed.bar.controller.app;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringScheduleJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.domain.ScheduleDto;
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


import java.time.LocalDateTime;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ScheduleControllerTest {

    public static final String BASE_URL = ApiPathConstants.V1_ROUTE + ApiPathConstants.SCHEDULE_ROUTE;

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
    private SpringScheduleJpaRepository scheduleRepository;

    @Autowired
    private SpringEmployeeJpaRepository employeeRepository;

    private EmployeeEntity testEmployee;
    private UserEntity managerUser;
    private String managerToken;

    @BeforeEach
    void setUp() {
        // Configurar ObjectMapper para manejar LocalTime
        objectMapper.registerModule(new JavaTimeModule());

        // Limpiar base de datos antes de cada test
        scheduleRepository.deleteAll();
        employeeRepository.deleteAll();
        userEntityRepository.deleteAll();

        // Crear empleado y usuario de prueba usando métodos helper
        testEmployee = createTestEmployee("María García", "maria.garcia@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);
        managerUser = createTestUser("manager-schedule@test.com", "password", EmployeeRole.MANAGER);
        managerToken = "Bearer " + jwtService.generateToken(managerUser.getEmail(), managerUser.getRole().name()).getAccessToken();
    }

    // Métodos helper para crear entidades de prueba
    private EmployeeEntity createTestEmployee(String name, String email, EmployeeRole role, EmployeeStatus status) {
        EmployeeEntity employee = EmployeeEntity.builder()
                .name(name)
                .email(email)
                .role(role)
                .salary(new java.math.BigDecimal("1800.00"))
                .hourlyRate(new java.math.BigDecimal("10.00")) // Added hourlyRate
                .status(status)
                .build();
        return employeeRepository.save(employee);
    }

    private UserEntity createTestUser(String email, String password, EmployeeRole role) {
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        return userEntityRepository.save(user);
    }

    private ScheduleDto createValidScheduleDto() {
       ScheduleDto scheduleDto = new ScheduleDto();
       scheduleDto.setEmployeeId(testEmployee.getId());
       scheduleDto.setStartTime(LocalDateTime.of(2025, 9, 30, 9, 0, 0));
       scheduleDto.setEndTime(LocalDateTime.of(2025, 9, 30, 17, 0, 0));
       return scheduleDto;
    }

    @Test
    void createSchedule_WithValidData_ShouldReturnCreatedSchedule() throws Exception {
        // Given
        ScheduleDto scheduleDto = createValidScheduleDto();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleDto)));

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.startTime", is("2025-09-30T09:00:00")))
                .andExpect(jsonPath("$.endTime", is("2025-09-30T17:00:00")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void createSchedule_WithInvalidEmployee_ShouldReturnNotFound() throws Exception {
        // Given
        ScheduleDto scheduleDto = createValidScheduleDto();
        scheduleDto.setEmployeeId(999L); // ID que no existe

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void createSchedule_WithEndTimeBeforeStartTime_ShouldReturnBadRequest() throws Exception {
        // Given - Hora de fin antes de hora de inicio
        ScheduleDto scheduleDto = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 9, 30, 17, 0, 0))
                .endTime(LocalDateTime.of(2025, 9, 30, 9, 0, 0)) // Hora fin antes de hora inicio
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_WithOverlappingSchedule_ShouldReturnBadRequest() throws Exception {
        // Given - Crear un horario existente
        ScheduleDto existingScheduleDto = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 9, 30, 9, 0, 0))
                .endTime(LocalDateTime.of(2025, 9, 30, 17, 0, 0))
                .build();

        mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingScheduleDto)))
                .andExpect(status().isCreated());

        // When - Intentar crear un horario que se superpone
        ScheduleDto overlappingScheduleDto = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 9, 30, 16, 0, 0)) // Se superpone con el existente
                .endTime(LocalDateTime.of(2025, 9, 30, 18, 0, 0))
                .build();

        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlappingScheduleDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_WithMissingEmployeeId_ShouldReturnBadRequest() throws Exception {
        // Given - Crear un ScheduleDto sin employeeId
        ScheduleDto scheduleDto = ScheduleDto.builder()
                .startTime(LocalDateTime.of(2025, 9, 30, 9, 0, 0))
                .endTime(LocalDateTime.of(2025, 9, 30, 17, 0, 0))
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSchedule_WithValidId_ShouldReturnSchedule() throws Exception {
        // Given - Crear un horario primero
        ScheduleDto scheduleDto = createValidScheduleDto();
        String responseJson = mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleDto)))
                .andReturn().getResponse().getContentAsString();

        ScheduleDto createdSchedule = objectMapper.readValue(responseJson, ScheduleDto.class);

        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/{id}", createdSchedule.getId())
                .header("Authorization", managerToken));

        // Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdSchedule.getId().intValue())))
                .andExpect(jsonPath("$.startTime", is("2025-09-30T09:00:00")))
                .andExpect(jsonPath("$.endTime", is("2025-09-30T17:00:00")));
    }

    @Test
    void getSchedule_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/{id}", 999L)
                .header("Authorization", managerToken));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getSchedule_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/{id}", "abc")
                .header("Authorization", managerToken));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSchedulesByEmployee_WithValidEmployee_ShouldReturnScheduleList() throws Exception {
        // Given - Crear varios horarios para el empleado
        createTestSchedulesForEmployee();

        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/employee/{employeeId}", testEmployee.getId())
                .header("Authorization", managerToken));

        // Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    void getSchedulesByEmployee_WithNonExistentEmployee_ShouldReturnNotFound() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/employee/{employeeId}", 999L)
                .header("Authorization", managerToken));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getSchedulesByEmployee_WithNoSchedules_ShouldReturnEmptyList() throws Exception {
        // Given - No crear horarios

        // When
        ResultActions response = mockMvc.perform(get(BASE_URL + "/employee/{employeeId}", testEmployee.getId())
                .header("Authorization", managerToken));

        // Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void updateSchedule_WithValidData_ShouldReturnUpdatedSchedule() throws Exception {
        // Given - Crear un horario primero
        ScheduleDto scheduleDto = createValidScheduleDto();
        String responseJson = mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleDto)))
                .andReturn().getResponse().getContentAsString();

        ScheduleDto createdSchedule = objectMapper.readValue(responseJson, ScheduleDto.class);

        // Preparar datos de actualización
        ScheduleDto updateDto = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 9, 30, 10, 0, 0))
                .endTime(LocalDateTime.of(2025, 9, 30, 18, 0, 0))
                .build();

        // When
        ResultActions response = mockMvc.perform(put(BASE_URL + "/{id}", createdSchedule.getId())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime", is("2025-09-30T10:00:00")))
                .andExpect(jsonPath("$.endTime", is("2025-09-30T18:00:00")));
    }

    @Test
    void updateSchedule_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        ScheduleDto updateDto = createValidScheduleDto();

        // When
        ResultActions response = mockMvc.perform(put(BASE_URL + "/{id}", 999L)
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSchedule_WithInvalidEmployee_ShouldReturnNotFound() throws Exception {
        // Given - Crear un horario primero
        ScheduleDto scheduleDto = createValidScheduleDto();
        String responseJson = mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleDto)))
                .andReturn().getResponse().getContentAsString();

        ScheduleDto createdSchedule = objectMapper.readValue(responseJson, ScheduleDto.class);

        // Preparar datos de actualización con empleado inválido
        ScheduleDto updateDto = createValidScheduleDto();
        updateDto.setEmployeeId(999L); // Empleado que no existe

        // When
        ResultActions response = mockMvc.perform(put(BASE_URL + "/{id}", createdSchedule.getId())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSchedule_WithEndTimeBeforeStartTime_ShouldReturnBadRequest() throws Exception {
        // Given - Crear un horario primero
        ScheduleDto scheduleDto = createValidScheduleDto();
        String responseJson = mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleDto)))
                .andReturn().getResponse().getContentAsString();

        ScheduleDto createdSchedule = objectMapper.readValue(responseJson, ScheduleDto.class);

        // When - Intentar actualizar con hora de fin antes de hora de inicio
        ScheduleDto updateDto = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 9, 30, 17, 0, 0))
                .endTime(LocalDateTime.of(2025, 9, 30, 9, 0, 0))
                .build();

        ResultActions response = mockMvc.perform(put(BASE_URL + "/{id}", createdSchedule.getId())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSchedule_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given - Crear un horario primero
        ScheduleDto scheduleDto = createValidScheduleDto();
        String responseJson = mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleDto)))
                .andReturn().getResponse().getContentAsString();

        ScheduleDto createdSchedule = objectMapper.readValue(responseJson, ScheduleDto.class);

        // When
        ResultActions response = mockMvc.perform(delete(BASE_URL + "/{id}", createdSchedule.getId())
                .header("Authorization", managerToken));

        // Then
        response.andDo(print())
                .andExpect(status().isNoContent());

        // Verify que el horario fue eliminado
        mockMvc.perform(get(BASE_URL + "/{id}", createdSchedule.getId())
                        .header("Authorization", managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSchedule_WithNightShift_ShouldReturnCreatedSchedule() throws Exception {
        // Given - Crear un horario nocturno (22:00 hoy a 06:00 mañana)
        ScheduleDto nightShiftDto = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 9, 30, 22, 0, 0)) // 10:00 PM
                .endTime(LocalDateTime.of(2025, 10, 1, 6, 0, 0))    // 6:00 AM siguiente día
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/employee/" + testEmployee.getId())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nightShiftDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startTime", is("2025-09-30T22:00:00")))
                .andExpect(jsonPath("$.endTime", is("2025-10-01T06:00:00")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void createSchedule_WithSameDayNightShift_ShouldReturnBadRequest() throws Exception {
        // Given - Crear un horario con hora de fin antes de hora de inicio en el mismo día
        ScheduleDto invalidNightShiftDto = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 9, 30, 22, 0, 0)) // 10:00 PM
                .endTime(LocalDateTime.of(2025, 9, 30, 6, 0, 0))    // 6:00 AM mismo día (inválido)
                .build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/employee/" + testEmployee.getId())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNightShiftDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_WithOverlappingNightShift_ShouldReturnBadRequest() throws Exception {
        // Given - Crear un horario de día primero (9:00 AM a 5:00 PM)
        ScheduleDto dayShiftDto = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 9, 30, 9, 0, 0))
                .endTime(LocalDateTime.of(2025, 9, 30, 17, 0, 0))
                .build();

        mockMvc.perform(post(BASE_URL + "/employee/" + testEmployee.getId())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dayShiftDto)))
                .andExpect(status().isCreated());

        // When - Intentar crear un horario nocturno que se superpone
        ScheduleDto overlappingNightShift = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 9, 30, 23, 0, 0)) // 11:00 PM
                .endTime(LocalDateTime.of(2025, 10, 1, 7, 0, 0))    // 7:00 AM siguiente día (se superpone)
                .build();

        ResultActions response = mockMvc.perform(post(BASE_URL + "/employee/" + testEmployee.getId())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlappingNightShift)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSchedule_WithNightShift_ShouldReturnUpdatedSchedule() throws Exception {
        // Given - Crear un horario primero
        ScheduleDto originalSchedule = createValidScheduleDto();
        String responseJson = mockMvc.perform(post(BASE_URL + "/employee/" + testEmployee.getId())
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalSchedule)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long createdScheduleId = objectMapper.readTree(responseJson).get("id").asLong();

        // When - Actualizarlo a un horario nocturno
        ScheduleDto updatedScheduleDto = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 9, 30, 22, 0, 0)) // 10:00 PM
                .endTime(LocalDateTime.of(2025, 10, 1, 6, 0, 0))    // 6:00 AM siguiente día
                .build();

        ResultActions response = mockMvc.perform(put(BASE_URL + "/" + createdScheduleId)
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedScheduleDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime", is("2025-09-30T22:00:00")))
                .andExpect(jsonPath("$.endTime", is("2025-10-01T06:00:00")));
    }

    @Test
    void deleteSchedule_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // When
        ResultActions response = mockMvc.perform(delete(BASE_URL + "/{id}", 999L)
                .header("Authorization", managerToken));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void createSchedule_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Given
        ScheduleDto scheduleDto = createValidScheduleDto();

        // When - Sin header de Authorization
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createSchedule_WithUserRoleWaiter_ShouldReturnForbidden() throws Exception {
        // Given - Crear usuario con rol WAITER (no tiene acceso)
        UserEntity waiterUser = createTestUser("waiter-schedule@test.com", "password", EmployeeRole.WAITER);
        String waiterToken = "Bearer " + jwtService.generateToken(waiterUser.getEmail(), waiterUser.getRole().name()).getAccessToken();

        ScheduleDto scheduleDto = createValidScheduleDto();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", waiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createSchedule_WithUserRoleAdmin_ShouldReturnSuccess() throws Exception {
        // Given - Crear usuario con rol ADMIN (debe tener acceso)
        UserEntity adminUser = createTestUser("admin-schedule@test.com", "password", EmployeeRole.ADMIN);
        String adminToken = "Bearer " + jwtService.generateToken(adminUser.getEmail(), adminUser.getRole().name()).getAccessToken();

        ScheduleDto scheduleDto = createValidScheduleDto();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isCreated());
    }

    private void createTestSchedulesForEmployee() throws Exception {
        // Crear horarios de lunes y martes
        ScheduleDto mondaySchedule = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 9, 30, 9, 0, 0))
                .endTime(LocalDateTime.of(2025, 9, 30, 17, 0, 0))
                .build();

        ScheduleDto tuesdaySchedule = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .startTime(LocalDateTime.of(2025, 10, 1, 10, 0, 0)) // Changed date to next day
                .endTime(LocalDateTime.of(2025, 10, 1, 18, 0, 0))  // Changed date to next day
                .build();

        mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mondaySchedule)));

        mockMvc.perform(post(BASE_URL + "/")
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tuesdaySchedule)));
    }
}