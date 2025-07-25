package com.example.simple.modules.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.simple.modules.permission.converter.PermissionConverter;
import com.example.simple.modules.permission.entity.PermissionEntity;
import com.example.simple.modules.permission.mapper.PermissionMapper;
import com.example.simple.modules.permission.vo.PermissionVO;
import com.example.simple.modules.role.mapper.RoleMapper;
import com.example.simple.modules.user.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionMapper permissionMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PermissionConverter permissionConverter = PermissionConverter.INSTANCE;

    public List<PermissionVO> getPermissionTree() {
        // 1. 查询所有权限
        List<PermissionEntity> allPermissions = permissionMapper.selectList(
                new LambdaQueryWrapper<PermissionEntity>().orderByAsc(PermissionEntity::getSortOrder)
        );

        // 2. 转换为VO，并用ID建立索引
        Map<Long, PermissionVO> permissionMap = allPermissions.stream()
                .map(permissionConverter::toPermissionVO)
                .collect(Collectors.toMap(PermissionVO::getId, p -> p));

        // 3. 构建树形结构
        List<PermissionVO> tree = permissionMap.values().stream()
                .filter(p -> {
                    if (p.getParentId() != 0 && permissionMap.containsKey(p.getParentId())) {
                        permissionMap.get(p.getParentId()).getChildren().add(p);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        return tree;
    }

    /**
     * 根据用户ID获取其所有权限标识符（包括角色和权限码）
     * @param userId 用户ID
     * @return 权限标识符集合
     */
    public Set<String> getAuthorityStringsByUserId(Long userId) {
        Set<String> permissions = new HashSet<>();

        // 1. 根据用户ID查询角色编码 (e.g., "ADMIN")
        List<String> roleCodes = roleMapper.selectRoleCodesByUserId(userId);
        if (!CollectionUtils.isEmpty(roleCodes)) {
            // 添加 "ROLE_" 前缀
            roleCodes.forEach(roleCode -> permissions.add("ROLE_" + roleCode));
        }

        // 2. 根据用户ID查询角色ID列表
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (!CollectionUtils.isEmpty(roleIds)) {
            // 3. 根据角色ID列表查询权限标识符 (e.g., "users:create")
            List<String> permissionCodes = permissionMapper.selectPermissionCodesByRoleIds(roleIds);
            if (!CollectionUtils.isEmpty(permissionCodes)) {
                permissions.addAll(permissionCodes);
            }
        }

        return permissions;
    }
}