package com.tableorder.admin.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.admin.TestSecurityConfig;
import com.tableorder.admin.auth.dto.LoginRequest;
import com.tableorder.admin.auth.dto.RegisterRequest;
import com.tableorder.admin.auth.dto.AdminResponse;
import com.tableorder.admin.auth.dto.TokenResponse;
import com.tableorder.core.exception.GlobalExceptionHandler;
import com.tableorder.core.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAuthController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class AdminAuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AdminAuthService adminAuthService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("POST /api/admin/auth/login - 성공")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest("test-store", "admin1", "password1");
        given(adminAuthService.login("test-store", "admin1", "password1"))
                .willReturn(new TokenResponse("jwt-token", 57600000L));

        mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-token"));
    }

    @Test
    @DisplayName("POST /api/admin/auth/register - 성공")
    void register_success() throws Exception {
        RegisterRequest request = new RegisterRequest("test-store", "newadmin", "password1");
        given(adminAuthService.register("test-store", "newadmin", "password1"))
                .willReturn(new AdminResponse(1L, 1L, "newadmin", LocalDateTime.now()));

        mockMvc.perform(post("/api/admin/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("newadmin"));
    }
}
