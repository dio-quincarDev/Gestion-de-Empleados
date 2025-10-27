package com.employed.bar.controller.app;

import com.employed.bar.domain.enums.BankAccount;
import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.enums.PaymentType;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.dto.domain.EmployeeDto;
import com.employed.bar.infrastructure.dto.payment.AchPaymentMethodDto;
import com.employed.bar.infrastructure.dto.payment.CashPaymentMethodDto;
import com.employed.bar.infrastructure.dto.payment.YappyPaymentMethodDto;
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

    private EmployeeEntity createTestEmployee(String name, String email, EmployeeRole role, EmployeeStatus status, PaymentType paymentType) {
        EmployeeEntity employee = new EmployeeEntity();
        employee.setName(name);
        employee.setEmail(email);
        employee.setRole(role);
        employee.setStatus(status);
        employee.setPaymentType(paymentType);
        if (paymentType == PaymentType.HOURLY) {
            employee.setHourlyRate(new BigDecimal("10.00"));
            employee.setSalary(BigDecimal.ZERO);
        } else {
            employee.setHourlyRate(BigDecimal.ZERO);
            employee.setSalary(new BigDecimal("2000.00"));
        }
        return employeeRepository.save(employee);
    }

    @Test
    void whenCreateEmployee_asAdmin_shouldSucceed() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(null, "New Employee", new BigDecimal("12.50"),
                EmployeeRole.WAITER, "new@test.com", BigDecimal.ZERO, "+50761234567",
                "ACTIVE", false, null, PaymentType.HOURLY, new CashPaymentMethodDto());

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Employee")))
                .andExpect(jsonPath("$.email", is("new@test.com")));
    }

    @Test
    void whenCreateEmployee_salaried_shouldSucceed() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(null, "Salaried Employee", BigDecimal.ZERO, EmployeeRole.MANAGER,
                "salaried@test.com", new BigDecimal("3000.00"), "+50761234567",
                "ACTIVE", false, null, PaymentType.SALARIED, new CashPaymentMethodDto());

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Salaried Employee")))
                .andExpect(jsonPath("$.paymentType", is("SALARIED")));
    }

    @Test
    void whenCreateEmployee_withExistingEmail_shouldFail() throws Exception {
        createTestEmployee("Existing Employee", "existing@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE, PaymentType.HOURLY);
        EmployeeDto employeeDto = new EmployeeDto(null, "New Employee", new BigDecimal("12.50"), EmployeeRole.WAITER, "existing@test.com", BigDecimal.ZERO, "+50761234567", "ACTIVE", false, null, PaymentType.HOURLY, new CashPaymentMethodDto());

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenGetEmployeeById_shouldReturnEmployee() throws Exception {
        EmployeeEntity employee = createTestEmployee("John Doe", "john.doe@test.com", EmployeeRole.BARTENDER, EmployeeStatus.ACTIVE, PaymentType.HOURLY);

        mockMvc.perform(get(BASE_URL + "/{id}", employee.getId())
                        .header("Authorization", managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employee.getId().intValue())))
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    void whenGetAllEmployees_shouldReturnEmployeeList() throws Exception {
        createTestEmployee("John Doe", "john.doe@test.com", EmployeeRole.BARTENDER, EmployeeStatus.ACTIVE, PaymentType.HOURLY);
        createTestEmployee("Jane Smith", "jane.smith@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE, PaymentType.HOURLY);

        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void whenUpdateEmployee_shouldSucceed() throws Exception {
        EmployeeEntity employee = createTestEmployee("Old Name", "old.email@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE, PaymentType.HOURLY);
        EmployeeDto updatedDto = new EmployeeDto(employee.getId(), "New Name", new BigDecimal("20.00"), EmployeeRole.BARTENDER, "new.email@test.com", BigDecimal.ZERO, "+50761234567", "INACTIVE", true, null, PaymentType.HOURLY, new CashPaymentMethodDto());

        mockMvc.perform(put(BASE_URL + "/{id}", employee.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk());
    }

    @Test
    void whenCreateHourlyEmployee_withNonZeroSalary_shouldFail() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(null, "Test", new BigDecimal("10"), EmployeeRole.WAITER, "test@test.com", new BigDecimal("100"), "+50761234567", "ACTIVE", false, null, PaymentType.HOURLY, new CashPaymentMethodDto());

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateSalariedEmployee_withZeroSalary_shouldFail() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(null, "Test", BigDecimal.ZERO, EmployeeRole.MANAGER, "test@test.com", BigDecimal.ZERO, "+50761234567", "ACTIVE", false, null, PaymentType.SALARIED, new CashPaymentMethodDto());

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateFromHourlyToSalaried_withZeroSalary_shouldFail() throws Exception {
        EmployeeEntity employee = createTestEmployee("Test", "test@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE, PaymentType.HOURLY);
        EmployeeDto updatedDto = new EmployeeDto(employee.getId(), "Updated", BigDecimal.ZERO, EmployeeRole.MANAGER, "test@test.com", BigDecimal.ZERO, "+50761234567", "ACTIVE", false, null, PaymentType.SALARIED, new CashPaymentMethodDto());

        mockMvc.perform(put(BASE_URL + "/{id}", employee.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateSalariedEmployee_withOvertimeAndZeroHourlyRate_shouldFail() throws Exception {
        EmployeeEntity employee = createTestEmployee("Test", "test@test.com", EmployeeRole.MANAGER, EmployeeStatus.ACTIVE, PaymentType.SALARIED);
        EmployeeDto updatedDto = new EmployeeDto(employee.getId(), "Updated", BigDecimal.ZERO, EmployeeRole.MANAGER, "test@test.com", new BigDecimal("3000"), "+50761234567", "ACTIVE", true, null, PaymentType.SALARIED, new CashPaymentMethodDto());

        mockMvc.perform(put(BASE_URL + "/{id}", employee.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateEmployee_withAchPayment_shouldReturnAchDetails() throws Exception {
        AchPaymentMethodDto achDto = new AchPaymentMethodDto("Banco General", "123456789", BankAccount.SAVINGS);
        EmployeeDto employeeDto = new EmployeeDto(null, "Ach Employee", new BigDecimal("15.00"), EmployeeRole.CHEF, "ach@test.com", BigDecimal.ZERO, "+50761234567", "ACTIVE", false, null, PaymentType.HOURLY, achDto);

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Ach Employee")))
                .andExpect(jsonPath("$.paymentMethod").exists())
                .andExpect(jsonPath("$.paymentMethod.type", is("ACH")))
                .andExpect(jsonPath("$.paymentMethod.bankName", is("Banco General")))
                .andExpect(jsonPath("$.paymentMethod.accountNumber", is("123456789")));
    }

    @Test
    void whenCreateEmployee_withYappyPayment_shouldReturnYappyDetails() throws Exception {
        YappyPaymentMethodDto yappyDto = new YappyPaymentMethodDto("65432100");
        EmployeeDto employeeDto = new EmployeeDto(null, "Yappy Employee", new BigDecimal("10.00"), EmployeeRole.DJ, "yappy@test.com", BigDecimal.ZERO, "+50761234567", "ACTIVE", false, null, PaymentType.HOURLY, yappyDto);

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Yappy Employee")))
                .andExpect(jsonPath("$.paymentMethod").exists())
                .andExpect(jsonPath("$.paymentMethod.type", is("YAPPY")))
                .andExpect(jsonPath("$.paymentMethod.phoneNumber", is("65432100")));
    }
}