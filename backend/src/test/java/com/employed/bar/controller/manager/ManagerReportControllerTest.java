package com.employed.bar.controller.manager;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.port.in.service.ManagerReportServicePort;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ManagerReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ManagerReportServicePort managerReportServicePort;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        userEntityRepository.deleteAll();
        UserEntity managerUser = createTestUser("manager@example.com", "password123", EmployeeRole.MANAGER);
        jwtToken = generateToken(managerUser.getEmail(), managerUser.getRole().name());
    }

    @Test
    void whenGenerateManagerWeeklyReport_withValidDates_shouldReturnOk() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 7);

        mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.REPORT_ROUTE + "/weekly")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Manager report generation triggered successfully."));

        verify(managerReportServicePort).generateAndSendManagerReport(startDate, endDate);
    }

    @Test
    void whenDownloadManagerWeeklyReportPdf_withValidDates_shouldReturnPdf() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 7);
        byte[] pdfBytes = "Dummy PDF content".getBytes();

        when(managerReportServicePort.generateManagerReportPdf(startDate, endDate)).thenReturn(pdfBytes);

        mockMvc.perform(get(ApiPathConstants.V1_ROUTE + ApiPathConstants.REPORT_ROUTE + "/weekly/pdf")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"manager_weekly_report.pdf\""))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    void whenGenerateManagerWeeklyReport_withoutToken_shouldReturnUnauthorized() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 7);

        mockMvc.perform(post(ApiPathConstants.V1_ROUTE + ApiPathConstants.REPORT_ROUTE + "/weekly")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isUnauthorized());
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
}

