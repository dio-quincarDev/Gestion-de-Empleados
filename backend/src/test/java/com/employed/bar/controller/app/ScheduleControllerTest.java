package com.employed.bar.controller.app;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.ScheduleEntity;
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    private ScheduleDto createValidScheduleDto() {
       ScheduleDto scheduleDto = new ScheduleDto();
       scheduleDto.setEmployeeId(testEmployee.getId());
       scheduleDto.setStartTime(LocalDateTime.now().withHour(9).withMinute(0).withSecond(0));
       scheduleDto.setEndTime(LocalDateTime.now().withHour(17).minusMinutes(0).withSecond(0));
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$.startTime", is("09:00:00")))
                .andExpect(jsonPath("$.endTime", is("17:00:00")))
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
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(17, 0))
                .endTime(LocalTime.of(9, 0)) // Hora fin antes de hora inicio
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
                .andExpect(jsonPath("$.dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$.startTime", is("09:00:00")))
                .andExpect(jsonPath("$.endTime", is("17:00:00")));
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
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .build();

        // When
        ResultActions response = mockMvc.perform(put(BASE_URL + "/{id}", createdSchedule.getId())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)));

        // Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayOfWeek", is("TUESDAY")))
                .andExpect(jsonPath("$.startTime", is("10:00:00")))
                .andExpect(jsonPath("$.endTime", is("18:00:00")));
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dayOfWeek", is("MONDAY")));
    }

    private void createTestSchedulesForEmployee() throws Exception {
        // Crear horarios de lunes y martes
        ScheduleDto mondaySchedule = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        ScheduleDto tuesdaySchedule = ScheduleDto.builder()
                .employeeId(testEmployee.getId())
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
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