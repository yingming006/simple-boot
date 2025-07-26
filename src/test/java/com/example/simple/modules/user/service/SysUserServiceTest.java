package com.example.simple.modules.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.simple.common.utils.AuthUtils;
import com.example.simple.exception.BusinessException;
import com.example.simple.modules.role.entity.RoleEntity;
import com.example.simple.modules.role.mapper.RoleMapper;
import com.example.simple.modules.user.entity.SysUserEntity;
import com.example.simple.modules.user.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SysUserServiceTest {

    @InjectMocks
    private SysUserService sysUserService;
    @Mock
    private SysUserMapper sysUserMapper;
    @Mock
    private RoleMapper roleMapper;

    @Test
    void deleteUsers_whenDeletingSuperAdmin_shouldThrowBusinessException() {
        // Arrange
        // 模拟 AuthUtils.getCurrentUserId() 返回一个非空值
        try (MockedStatic<AuthUtils> mockedAuth = Mockito.mockStatic(AuthUtils.class)) {
            mockedAuth.when(AuthUtils::getCurrentUserId).thenReturn(1L);

            SysUserEntity superAdmin = new SysUserEntity();
            superAdmin.setId(2L);
            superAdmin.setUsername("superadmin");

            when(sysUserMapper.selectByIds(List.of(2L))).thenReturn(List.of(superAdmin));
            when(roleMapper.selectRoleCodesByUserId(2L)).thenReturn(List.of("SUPER_ADMIN"));

            // Act & Assert
            BusinessException ex = assertThrows(BusinessException.class, () -> {
                sysUserService.deleteUsers(List.of(2L));
            });
            assertTrue(ex.getMessage().contains("是超级管理员，不能被删除"));
        }
    }

    @Test
    void assignRoles_whenRemovingSuperAdminRoleFromSuperAdmin_shouldThrowBusinessException() {
        // Arrange
        SysUserEntity superAdminUser = new SysUserEntity();
        superAdminUser.setId(1L);
        RoleEntity superAdminRole = new RoleEntity();
        superAdminRole.setId(99L); // 假设超级管理员角色ID是99

        when(sysUserMapper.selectById(1L)).thenReturn(superAdminUser);
        when(roleMapper.selectRoleCodesByUserId(1L)).thenReturn(List.of("SUPER_ADMIN"));
        when(roleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(superAdminRole);

        // 尝试分配一个不包含超级管理员角色的列表
        List<Long> newRoleIds = List.of(100L, 101L);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            sysUserService.assignRoles(1L, newRoleIds);
        });
        assertTrue(ex.getMessage().contains("不能移除超级管理员的 [SUPER_ADMIN] 角色"));
    }
}