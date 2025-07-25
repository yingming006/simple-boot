package com.example.simple.modules.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.simple.common.dto.PageQueryDTO;
import com.example.simple.common.utils.AuthUtils;
import com.example.simple.common.vo.PageVO;
import com.example.simple.exception.BusinessException;
import com.example.simple.modules.auth.service.AuthService;
import com.example.simple.modules.auth.dto.SysUserRegisterDTO;
import com.example.simple.modules.role.entity.RoleEntity;
import com.example.simple.modules.role.mapper.RoleMapper;
import com.example.simple.modules.user.converter.SysUserConverter;
import com.example.simple.modules.user.dto.SysUserCreateDTO;
import com.example.simple.modules.user.dto.SysUserDTO;
import com.example.simple.modules.user.dto.SysUserPasswordUpdateDTO;
import com.example.simple.modules.user.dto.SysUserUpdateDTO;
import com.example.simple.modules.user.entity.SysUserEntity;
import com.example.simple.modules.user.entity.SysUserRoleEntity;
import com.example.simple.modules.user.mapper.SysUserMapper;
import com.example.simple.modules.user.mapper.SysUserRoleMapper;
import com.example.simple.modules.user.vo.SysUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SysUserService {

    private final AuthService authService;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserConverter sysUserConverter;
    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;

    @Transactional
    public void register(SysUserRegisterDTO registerDTO) {
        LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserEntity::getUsername, registerDTO.getUsername());
        if (sysUserMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("用户已存在");
        }

        SysUserEntity newUser = new SysUserEntity();
        newUser.setUsername(registerDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        newUser.setNickname(StringUtils.hasText(registerDTO.getNickname()) ? registerDTO.getNickname() : "用户_" + UUID.randomUUID().toString().substring(0, 6));

        sysUserMapper.insert(newUser);
    }

    @Transactional(readOnly = true)
    public SysUserVO getUserInfo(Long userId) {
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return sysUserConverter.toUserVO(user);
    }

    @Transactional(readOnly = true)
    public PageVO<SysUserVO> getUserPage(SysUserDTO queryDTO, PageQueryDTO pageQueryDTO) {
        Page<SysUserEntity> page = new Page<>(pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());

        LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(queryDTO.getNickname()), SysUserEntity::getNickname, queryDTO.getNickname());
        queryWrapper.like(StringUtils.hasText(queryDTO.getUsername()), SysUserEntity::getUsername, queryDTO.getUsername());
        queryWrapper.orderByDesc(SysUserEntity::getCreateTime);

        Page<SysUserEntity> userPage = sysUserMapper.selectPage(page, queryWrapper);

        return PageVO.of(userPage.convert(sysUserConverter::toUserVO));
    }

    @Transactional
    public void createUser(SysUserCreateDTO createDTO) {
        LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserEntity::getUsername, createDTO.getUsername());
        if (sysUserMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("用户已存在");
        }
        createDTO.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        SysUserEntity newUser = sysUserConverter.toUser(createDTO);
        sysUserMapper.insert(newUser);
    }

    @Transactional
    public void updateUser(Long userId, SysUserUpdateDTO updateDTO) {
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        sysUserConverter.userUpdateDtoToUser(updateDTO, user);
        sysUserMapper.updateById(user);
    }

    @Transactional
    public void updatePassword(Long userId, SysUserPasswordUpdateDTO passwordUpdateDTO) {
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        SysUserEntity userToUpdate = new SysUserEntity();
        userToUpdate.setId(userId);
        userToUpdate.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        sysUserMapper.updateById(userToUpdate);
        authService.invalidateUserTokens(userId);
    }

    @Transactional
    public void deleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("用户ID列表不能为空");
        }
        Long currentUserId = AuthUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException("用户未登录");
        }
        if (ids.contains(currentUserId)) {
            throw new BusinessException("操作失败：不能删除自己的账户");
        }
        List<SysUserEntity> usersToDelete = sysUserMapper.selectByIds(ids);
        if (usersToDelete.isEmpty()) {
            return;
        }
        for (SysUserEntity user : usersToDelete) {
            List<String> roleCodes = roleMapper.selectRoleCodesByUserId(user.getId());
            if (roleCodes != null && roleCodes.contains(AuthUtils.ROLE_SUPER_ADMIN)) {
                String errorMessage = String.format("操作失败：用户 '%s' 是超级管理员，不能被删除", user.getUsername());
                throw new BusinessException(errorMessage);
            }
        }
        sysUserMapper.deleteByIds(ids);
    }

    @Transactional(readOnly = true)
    public List<Long> getRoleIdsByUserId(Long userId) {
        return sysUserRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        List<String> currentRoleCodes = roleMapper.selectRoleCodesByUserId(userId);
        if (currentRoleCodes.contains(AuthUtils.ROLE_SUPER_ADMIN)) {
            RoleEntity superAdminRole = roleMapper.selectOne(new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getCode, AuthUtils.ROLE_SUPER_ADMIN));
            if (superAdminRole != null && (roleIds == null || !roleIds.contains(superAdminRole.getId()))) {
                throw new BusinessException("操作失败：不能移除超级管理员的 [SUPER_ADMIN] 角色");
            }
        }

        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRoleEntity>().eq(SysUserRoleEntity::getUserId, userId));

        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRoleEntity> newRelations = roleIds.stream()
                    .map(roleId -> new SysUserRoleEntity(userId, roleId))
                    .toList();
            sysUserRoleMapper.insertBatch(newRelations);
        }

        authService.invalidateUserTokens(userId);
    }
}