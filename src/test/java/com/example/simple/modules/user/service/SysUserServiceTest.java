package com.example.simple.modules.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.simple.exception.BusinessException;
import com.example.simple.modules.user.converter.SysUserConverter;
import com.example.simple.modules.user.dto.SysUserCreateDTO;
import com.example.simple.modules.user.entity.SysUserEntity;
import com.example.simple.modules.user.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysUserService 的单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysUserServiceTest {

    @InjectMocks
    private SysUserService sysUserService;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SysUserConverter sysUserConverter;

    @Test
    void createUser_whenUsernameIsNew_shouldInsertUser() {
        // 1. 准备 (Arrange)
        SysUserCreateDTO createDTO = new SysUserCreateDTO();
        createDTO.setUsername("newUser");
        createDTO.setPassword("password123");

        when(sysUserMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        // 当转换器被调用时，返回一个模拟的实体
        when(sysUserConverter.toUser(createDTO)).thenReturn(new SysUserEntity());


        // 2. 执行 (Act)
        sysUserService.createUser(createDTO);


        // 3. 断言 (Assert)
        verify(passwordEncoder, times(1)).encode("password123");
        verify(sysUserMapper, times(1)).insert(any(SysUserEntity.class));
    }

    @Test
    void createUser_whenUsernameExists_shouldThrowBusinessException() {
        // 1. 准备 (Arrange)
        SysUserCreateDTO createDTO = new SysUserCreateDTO();
        createDTO.setUsername("existingUser");
        createDTO.setPassword("password123");

        when(sysUserMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);


        // 2. 执行 & 断言 (Act & Assert)
        assertThrows(BusinessException.class, () -> sysUserService.createUser(createDTO));

        verify(sysUserMapper, never()).insert(any(SysUserEntity.class));
    }
}