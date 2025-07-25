package com.example.simple.modules.role.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.simple.common.dto.PageQueryDTO;
import com.example.simple.common.utils.AuthUtils;
import com.example.simple.common.vo.PageVO;
import com.example.simple.exception.BusinessException;
import com.example.simple.modules.permission.entity.PermissionEntity;
import com.example.simple.modules.permission.mapper.PermissionMapper;
import com.example.simple.modules.role.converter.RoleConverter;
import com.example.simple.modules.role.dto.RoleCreateDTO;
import com.example.simple.modules.role.dto.RoleDTO;
import com.example.simple.modules.role.dto.RoleUpdateDTO;
import com.example.simple.modules.role.entity.RoleEntity;
import com.example.simple.modules.role.entity.RolePermissionEntity;
import com.example.simple.modules.role.mapper.RoleMapper;
import com.example.simple.modules.role.mapper.RolePermissionMapper;
import com.example.simple.modules.role.vo.RoleVO;
import com.example.simple.modules.user.entity.SysUserRoleEntity;
import com.example.simple.modules.user.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PermissionMapper permissionMapper;
    private final RoleConverter roleConverter = RoleConverter.INSTANCE;

    private static final List<String> CORE_PERMISSIONS = List.of("users:assign_roles", "roles:assign_permissions");

    @Transactional(readOnly = true)
    public PageVO<RoleVO> getRolePage(RoleDTO queryDTO, PageQueryDTO pageQueryDTO) {
        Page<RoleEntity> page = new Page<>(pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());

        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(queryDTO.getName()), RoleEntity::getName, queryDTO.getName());
        queryWrapper.like(StringUtils.hasText(queryDTO.getCode()), RoleEntity::getCode, queryDTO.getCode());
        queryWrapper.orderByDesc(RoleEntity::getCreateTime);

        Page<RoleEntity> rolePage = roleMapper.selectPage(page, queryWrapper);

        return PageVO.of(rolePage.convert(roleConverter::toRoleVO));
    }

    @Transactional(readOnly = true)
    public RoleVO getRoleById(Long id) {
        RoleEntity role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return roleConverter.toRoleVO(role);
    }

    @Transactional
    public void createRole(RoleCreateDTO createDTO) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleEntity::getCode, createDTO.getCode());
        if (roleMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }
        RoleEntity newRole = roleConverter.toRole(createDTO);
        roleMapper.insert(newRole);
    }

    @Transactional
    public void updateRole(Long id, RoleUpdateDTO updateDTO) {
        RoleEntity role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        roleConverter.updateRoleFromDto(updateDTO, role);
        roleMapper.updateById(role);
    }

    @Transactional
    public void deleteRoles(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("ID列表不能为空");
        }
        for (Long roleId : ids) {
            LambdaQueryWrapper<SysUserRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUserRoleEntity::getRoleId, roleId);
            if (userRoleMapper.selectCount(queryWrapper) > 0) {
                RoleEntity role = roleMapper.selectById(roleId);
                String roleName = (role != null) ? role.getName() : "ID:" + roleId;
                throw new BusinessException("角色 '" + roleName + "' 已被用户关联，无法删除");
            }
        }
        roleMapper.deleteByIds(ids);
    }

    @Transactional(readOnly = true)
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<RolePermissionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermissionEntity::getRoleId, roleId);
        queryWrapper.select(RolePermissionEntity::getPermissionId);

        List<RolePermissionEntity> relations = rolePermissionMapper.selectList(queryWrapper);
        if (relations.isEmpty()) {
            return Collections.emptyList();
        }
        return relations.stream().map(RolePermissionEntity::getPermissionId).collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(cacheNames = "permissions", allEntries = true)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        RoleEntity role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        if (AuthUtils.ROLE_SUPER_ADMIN.equals(role.getCode())) {
            if (permissionIds == null || permissionIds.isEmpty()) {
                throw new BusinessException("操作失败：不能移除超级管理员的所有权限");
            }
            List<PermissionEntity> newPermissions = permissionMapper.selectBatchIds(permissionIds);
            Set<String> newPermissionCodes = newPermissions.stream().map(PermissionEntity::getCode).collect(Collectors.toSet());
            if (!newPermissionCodes.containsAll(CORE_PERMISSIONS)) {
                throw new BusinessException("操作失败：不能移除超级管理员的核心管理权限");
            }
        }

        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermissionEntity>().eq(RolePermissionEntity::getRoleId, roleId));

        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermissionEntity> newRelations = permissionIds.stream()
                    .map(permId -> new RolePermissionEntity(roleId, permId))
                    .toList();
            rolePermissionMapper.insertBatch(newRelations);
        }
    }
}