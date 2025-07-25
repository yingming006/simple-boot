package com.example.simple.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.simple.security.principal.SecurityPrincipal;
import com.example.simple.modules.permission.service.PermissionService;
import com.example.simple.modules.user.entity.SysUserEntity;
import com.example.simple.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 针对后台系统（SysUser）用户的 UserDetailsService 实现
 */
@Service("sysUserDetailsService") // 明确指定Bean的名称
@RequiredArgsConstructor
public class SysUserDetailsService implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final PermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 根据用户名查询用户
        LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserEntity::getUsername, username);
        SysUserEntity user = sysUserMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new UsernameNotFoundException("用户 '" + username + "' 不存在");
        }

        // 2. 调用公共方法查询权限
        Set<String> permissions = permissionService.getAuthorityStringsByUserId(user.getId());

        return new SecurityPrincipal(user.getId(), user.getUsername(), user.getPassword(), permissions);
    }
}