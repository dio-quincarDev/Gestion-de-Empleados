package com.employed.bar.controller.app;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.dto.domain.EmployeeDto;
import com.employed.bar.infrastructure.dto.payment.CashPaymentMethodDto;
import com.employed.bar.infrastructure.dto.request.UpdateHourlyRateRequest;
import com.employed.bar.infrastructure.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmployeeControllerTest {

    private static final String BASE_URL = "/v1/employees";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringEmployeeJpaRepository employeeRepository;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String managerToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        userEntityRepository.deleteAll();

        UserEntity adminUser = createTestUser("admin@test.com", "password", EmployeeRole.ADMIN);
        UserEntity managerUser = createTestUser("manager@test.com", "password", EmployeeRole.MANAGER);
        UserEntity regularUser = createTestUser("user@test.com", "password", EmployeeRole.WAITER);

        adminToken = "Bearer " + jwtService.generateToken(adminUser.getEmail(), adminUser.getRole().name()).getAccessToken();
        managerToken = "Bearer " + jwtService.generateToken(managerUser.getEmail(), managerUser.getRole().name()).getAccessToken();
        userToken = "Bearer " + jwtService.generateToken(regularUser.getEmail(), regularUser.getRole().name()).getAccessToken();
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

    private EmployeeEntity createTestEmployee(String name, String email, EmployeeRole role, EmployeeStatus status) {
        EmployeeEntity employee = new EmployeeEntity();
        employee.setName(name);
        employee.setEmail(email);
        employee.setRole(role);
        employee.setStatus(status);
        employee.setHourlyRate(new BigDecimal("10.00"));
        employee.setSalary(new BigDecimal("2000.00")); // Added salary
        return employeeRepository.save(employee);
    }

    @Test
    void whenCreateEmployee_asAdmin_shouldSucceed() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(null, "New Employee", new BigDecimal("12.50"), EmployeeRole.WAITER, "new@test.com", new BigDecimal("2500.00"), "ACTIVE", false, null, new CashPaymentMethodDto());

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Employee")))
                .andExpect(jsonPath("$.email", is("new@test.com")));
    }

    @Test
    void whenCreateEmployee_withExistingEmail_shouldFail() throws Exception {
        createTestEmployee("Existing Employee", "existing@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);
        EmployeeDto employeeDto = new EmployeeDto(null, "New Employee", new BigDecimal("12.50"), EmployeeRole.WAITER, "existing@test.com", new BigDecimal("2500.00"), "ACTIVE", false, null, new CashPaymentMethodDto());

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenGetEmployeeById_shouldReturnEmployee() throws Exception {
        EmployeeEntity employee = createTestEmployee("John Doe", "john.doe@test.com", EmployeeRole.BARTENDER, EmployeeStatus.ACTIVE);

        mockMvc.perform(get(BASE_URL + "/{id}", employee.getId())
                        .header("Authorization", managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employee.getId().intValue())))
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    void whenGetEmployeeById_nonExistent_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", 999L)
                        .header("Authorization", managerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetAllEmployees_shouldReturnEmployeeList() throws Exception {
        createTestEmployee("John Doe", "john.doe@test.com", EmployeeRole.BARTENDER, EmployeeStatus.ACTIVE);
        createTestEmployee("Jane Smith", "jane.smith@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);

        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void whenUpdateEmployee_shouldSucceed() throws Exception {
        EmployeeEntity employee = createTestEmployee("Old Name", "old.email@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);
        EmployeeDto updatedDto = new EmployeeDto(employee.getId(), "New Name", new BigDecimal("20.00"), EmployeeRole.BARTENDER, "new.email@test.com", new BigDecimal("3000.00"), "INACTIVE", true, null, new CashPaymentMethodDto());

        mockMvc.perform(put(BASE_URL + "/{id}", employee.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Name")))
                .andExpect(jsonPath("$.email", is("new.email@test.com")))
                .andExpect(jsonPath("$.status", is("INACTIVE")));
    }

    @Test
    void whenUpdateHourlyRate_shouldSucceed() throws Exception {
        EmployeeEntity employee = createTestEmployee("Rate Employee", "rate.employee@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);
        UpdateHourlyRateRequest request = new UpdateHourlyRateRequest();
        request.setHourlyRate(new BigDecimal("99.99"));

        mockMvc.perform(patch(BASE_URL + "/{id}/hourly-rate", employee.getId())
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hourlyRate", is(99.99)));
    }

    @Test
    void whenDeleteEmployee_shouldSucceed() throws Exception {
        EmployeeEntity employee = createTestEmployee("To Delete", "to.delete@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);

        mockMvc.perform(delete(BASE_URL + "/{id}", employee.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/{id}", employee.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenSearchEmployees_byStatus_shouldReturnFilteredList() throws Exception {
        createTestEmployee("Active Employee", "active@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);
        createTestEmployee("Inactive Employee", "inactive@test.com", EmployeeRole.BARTENDER, EmployeeStatus.INACTIVE);

        mockMvc.perform(get(BASE_URL + "/search")
                        .header("Authorization", managerToken)
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Inactive Employee")));
    }

    @Test
    void whenSearchEmployees_byRole_shouldReturnFilteredList() throws Exception {
        createTestEmployee("Waiter One", "waiter1@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);
        createTestEmployee("Bartender One", "bartender1@test.com", EmployeeRole.BARTENDER, EmployeeStatus.ACTIVE);

        mockMvc.perform(get(BASE_URL + "/search")
                        .header("Authorization", managerToken)
                        .param("role", "BARTENDER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Bartender One")));
    }

    @Test
    void whenAccessEndpoint_asUser_shouldBeForbidden() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAccessEndpoint_withoutToken_shouldBeUnauthorized() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    // --- Edge Cases ---

    @Test
    void whenUpdateEmployee_toEmailOfAnotherEmployee_shouldFail() throws Exception {
        EmployeeEntity employeeA = createTestEmployee("Employee A", "a@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);
        EmployeeEntity employeeB = createTestEmployee("Employee B", "b@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);

        EmployeeDto updatedDto = new EmployeeDto(employeeA.getId(), "Employee A Updated", new BigDecimal("20.00"), EmployeeRole.BARTENDER, employeeB.getEmail(), new BigDecimal("3000.00"), "ACTIVE", true, null, new CashPaymentMethodDto());

        mockMvc.perform(put(BASE_URL + "/{id}", employeeA.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenCreateEmployee_withNegativeHourlyRate_shouldFail() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(null, "Negative Rate Employee", new BigDecimal("-10.00"), EmployeeRole.WAITER, "negative@test.com", new BigDecimal("2500.00"), "ACTIVE", false, null, new CashPaymentMethodDto());

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenSearchEmployees_byEmptyName_shouldReturnAll() throws Exception {
        createTestEmployee("John Doe", "john.doe@test.com", EmployeeRole.BARTENDER, EmployeeStatus.ACTIVE);
        createTestEmployee("Jane Smith", "jane.smith@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);

        mockMvc.perform(get(BASE_URL + "/search")
                        .header("Authorization", managerToken)
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}