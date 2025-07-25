package com.example.simple.modules.user.controller;

import com.example.simple.BaseIntegrationTest;
import com.example.simple.modules.user.dto.SysUserCreateDTO;
import com.example.simple.modules.user.entity.SysUserEntity;
import com.example.simple.modules.user.mapper.SysUserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser; // 引入 WithMockUser
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class SysUserControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 测试场景：当尝试创建一个已存在的用户名时，
     * 期望 @UniqueUsername 注解生效，并返回 400 错误。
     */
    @Test
    // 使用 @WithMockUser 模拟一个已登录的用户，并赋予其 'users:create' 权限
    @WithMockUser(authorities = "users:create")
    void createUser_whenUsernameExists_shouldReturnBadRequest() throws Exception {
        // 1. 准备 (Arrange): 在数据库中预先插入一个用户
        SysUserEntity existingUser = new SysUserEntity();
        existingUser.setUsername("existingUser");
        existingUser.setPassword(passwordEncoder.encode("any_password"));
        sysUserMapper.insert(existingUser);

        // 准备一个与已存在用户同名的 DTO
        SysUserCreateDTO createDTO = new SysUserCreateDTO();
        createDTO.setUsername("existingUser");
        createDTO.setPassword("new_password");
        createDTO.setNickname("New User");

        // 2. 执行 (Act) & 断言 (Assert)
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                // 期望 HTTP 状态码为 400 Bad Request
                .andExpect(status().isBadRequest())
                // 期望返回的 JSON 中，message 字段包含我们的自定义错误信息
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }
}