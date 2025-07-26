package com.example.simple.modules.auth.controller;

import com.example.simple.BaseIntegrationTest;
import com.example.simple.modules.auth.dto.SysUserRegisterDTO;
import com.example.simple.modules.role.dto.RoleCreateDTO;
import com.example.simple.modules.role.service.RoleService;
import com.example.simple.modules.user.service.SysUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RoleService roleService;

    @Test
    void login_withCorrectCredentials_shouldReturnOkAndToken() throws Exception {
        // Arrange: 在测试方法内部准备数据
        SysUserRegisterDTO registerDTO = new SysUserRegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("password123");
        sysUserService.register(registerDTO);

        Map<String, String> loginRequest = Map.of("username", "testuser", "password", "password123");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", notNullValue()));
    }

    @Test
    @WithMockUser
    void accessProtectedEndpoint_withoutRequiredAuthority_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "roles:list")
    void accessProtectedEndpoint_withRequiredAuthority_shouldReturnOk() throws Exception {
        // Arrange: 在同一个事务内准备数据
        RoleCreateDTO createDTO = new RoleCreateDTO();
        createDTO.setName("Test Role");
        createDTO.setCode("TEST_ROLE");
        roleService.createRole(createDTO);

        // Act & Assert
        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records", hasSize(1)))
                .andExpect(jsonPath("$.data.records[0].name").value("Test Role"));
    }
}