package com.employed.bar.controller.app;

import com.employed.bar.application.service.AttendanceApplicationService;
import com.employed.bar.domain.enums.AttendanceStatus;
import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.exceptions.InvalidAttendanceDataException;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

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

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
        employeeRepository.deleteAll();
        userEntityRepository.deleteAll();

        UserEntity managerUser = createTestUser("manager-attendance@test.com", "password", EmployeeRole.MANAGER);
        managerToken = "Bearer " + jwtService.generateToken(managerUser.getEmail(), managerUser.getRole().name()).getAccessToken();

        testEmployee = createTestEmployee("Test Employee", "employee@test.com", EmployeeRole.WAITER, EmployeeStatus.ACTIVE);
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
        employee.setSalary(new BigDecimal("2000.00"));
        return employeeRepository.save(employee);
    }

    @Test
    void whenRegisterAttendance_shouldSucceed() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(testEmployee.getId());
        attendanceDto.setDate(LocalDate.now());
        attendanceDto.setEntryTime(LocalTime.of(9, 0));
        attendanceDto.setExitTime(LocalTime.of(17, 0));
        attendanceDto.setStatus(AttendanceStatus.PRESENT);

        when(attendanceApplicationService.registerAttendance(any(AttendanceRecordClass.class))).thenReturn(new AttendanceRecordClass());

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isOk());
    }

    @Test
    void whenRegisterAttendance_withNullEmployeeId_shouldReturnBadRequest() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        // EmployeeId is intentionally not set

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetAttendanceList_shouldReturnRecords() throws Exception {
        when(attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(new AttendanceRecordClass()));

        mockMvc.perform(get(BASE_URL + "/list")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("startDate", LocalDate.now().minusDays(2).toString())
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenGetAttendanceList_withNoRecords_shouldReturnEmptyList() throws Exception {
        when(attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE_URL + "/list")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("startDate", LocalDate.now().minusDays(2).toString())
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

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

    @Test
    void whenAccessEndpoint_withoutToken_shouldBeUnauthorized() throws Exception {
        mockMvc.perform(get(BASE_URL + "/list")
                        .param("employeeId", "1")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-01-31"))
                .andExpect(status().isUnauthorized());
    }

    // --- Edge Cases ---

    @Test
    void whenGetAttendanceList_withInvalidDateRange_shouldReturnBadRequest() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.minusDays(1); // End date is before start date

        when(attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new IllegalArgumentException("Invalid date range"));

        mockMvc.perform(get(BASE_URL + "/list")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenRegisterAttendance_withExitTimeBeforeEntryTime_shouldReturnBadRequest() throws Exception {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setEmployeeId(testEmployee.getId());
        attendanceDto.setDate(LocalDate.now());
        attendanceDto.setEntryTime(LocalTime.of(17, 0)); // Entry: 5 PM
        attendanceDto.setExitTime(LocalTime.of(9, 0));   // Exit: 9 AM
        attendanceDto.setStatus(AttendanceStatus.PRESENT);

        when(attendanceApplicationService.registerAttendance(any(AttendanceRecordClass.class)))
                .thenThrow(new InvalidAttendanceDataException("Exit time cannot be before entry time."));

        mockMvc.perform(post(BASE_URL + "/")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isBadRequest()); // Expecting 400 due to InvalidAttendanceDataException handler
    }

    @Test
    void whenGetAttendanceList_withMalformedDate_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/list")
                        .header("Authorization", managerToken)
                        .param("employeeId", testEmployee.getId().toString())
                        .param("startDate", "invalid-date-format")
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isBadRequest());
    }
}
