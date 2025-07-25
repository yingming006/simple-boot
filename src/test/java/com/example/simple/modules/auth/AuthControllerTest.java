package com.example.simple.modules.auth;

import com.example.simple.modules.auth.domain.SysUserRegisterDTO;
import com.example.simple.modules.user.service.SysUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SysUserService sysUserService;

    @BeforeEach
    void setUp() {
        SysUserRegisterDTO registerDTO = new SysUserRegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("password123");
        registerDTO.setNickname("Test User");
        sysUserService.register(registerDTO);
    }

    @Test
    void login_withCorrectCredentials_shouldReturnOkAndToken() throws Exception {
        Map<String, String> loginRequest = Map.of(
                "username", "testuser",
                "password", "password123"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", notNullValue()));
    }

    @Test
    void login_withIncorrectCredentials_shouldReturnUnauthorized() throws Exception {
        Map<String, String> loginRequest = Map.of(
                "username", "testuser",
                "password", "wrongpassword"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }
}