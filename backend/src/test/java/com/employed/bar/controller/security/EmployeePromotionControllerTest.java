package com.employed.bar.controller.security;

import com.employed.bar.BarempleadosApplication;
import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.in.controller.security.EmployeePromotionController;
import com.employed.bar.infrastructure.mail.TestMailConfig;
import com.employed.bar.infrastructure.security.user.EmployeeRoleChangeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BarempleadosApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMailConfig.class)
public class EmployeePromotionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeRoleChangeService employeeRoleChangeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeePromotionController.PasswordRequest passwordRequest;

    @BeforeEach
    void setUp() {
        passwordRequest = new EmployeePromotionController.PasswordRequest("newPassword");
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void whenPromoteEmployeeToAdminAsManager_thenOk() throws Exception {
        Long employeeId = 1L;
        doNothing().when(employeeRoleChangeService).handleRoleChange(employeeId, EmployeeRole.ADMIN, passwordRequest.getPassword());

        mockMvc.perform(post("/v1/users/promotion/employee/{employeeId}/to-admin", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void whenPromoteEmployeeToAdminAsAdmin_thenForbidden() throws Exception {
        Long employeeId = 1L;

        mockMvc.perform(post("/v1/users/promotion/employee/{employeeId}/to-admin", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void whenPromoteEmployeeToAdminAsUser_thenForbidden() throws Exception {
        Long employeeId = 1L;

        mockMvc.perform(post("/v1/users/promotion/employee/{employeeId}/to-admin", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void whenPromoteEmployeeToAdminWithNoPassword_thenBadRequest() throws Exception {
        Long employeeId = 1L;
        EmployeePromotionController.PasswordRequest emptyPasswordRequest = new EmployeePromotionController.PasswordRequest("");

        mockMvc.perform(post("/v1/users/promotion/employee/{employeeId}/to-admin", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyPasswordRequest)))
                .andExpect(status().isBadRequest());
    }
}
