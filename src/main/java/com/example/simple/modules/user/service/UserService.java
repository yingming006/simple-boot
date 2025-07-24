package com.example.simple.modules.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.simple.common.dto.PageQueryDTO;
import com.example.simple.common.utils.AuthUtils;
import com.example.simple.common.vo.PageVO;
import com.example.simple.exception.BusinessException;
import com.example.simple.modules.auth.AuthService;
import com.example.simple.modules.auth.domain.UserLoginDTO;
import com.example.simple.modules.auth.domain.UserRegisterDTO;
import com.example.simple.modules.user.converter.UserConverter;
import com.example.simple.modules.user.dto.UserCreateDTO;
import com.example.simple.modules.user.dto.UserDTO;
import com.example.simple.modules.user.dto.UserPasswordUpdateDTO;
import com.example.simple.modules.user.dto.UserUpdateDTO;
import com.example.simple.modules.user.entity.UserEntity;
import com.example.simple.modules.user.mapper.UserMapper;
import com.example.simple.modules.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthService authService;
    private final UserMapper userMapper;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserRegisterDTO registerDTO) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUsername, registerDTO.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("用户已存在");
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(registerDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        newUser.setNickname(StringUtils.hasText(registerDTO.getNickname()) ? registerDTO.getNickname() : "用户_" + UUID.randomUUID().toString().substring(0, 6));

        userMapper.insert(newUser);
    }

    @Transactional(readOnly = true)
    public UserVO getUserInfo(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        return userConverter.toUserVO(user);
    }

    @Transactional(readOnly = true)
    public PageVO<UserVO> getUserPage(UserDTO queryDTO, PageQueryDTO pageQueryDTO) {
        Page<UserEntity> page = new Page<>(pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());

        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(queryDTO.getNickname()), UserEntity::getNickname, queryDTO.getNickname());
        queryWrapper.like(StringUtils.hasText(queryDTO.getUsername()), UserEntity::getUsername, queryDTO.getUsername());
        queryWrapper.orderByDesc(UserEntity::getCreateTime);

        Page<UserEntity> userPage = userMapper.selectPage(page, queryWrapper);

        return PageVO.of(userPage.convert(userConverter::toUserVO));
    }

    @Transactional
    public void createUser(UserCreateDTO createDTO) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUsername, createDTO.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("用户已存在");
        }

        createDTO.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        UserEntity newUser = userConverter.toUser(createDTO);

        userMapper.insert(newUser);
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateDTO updateDTO) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        userConverter.userUpdateDtoToUser(updateDTO, user);

        userMapper.updateById(user);
    }

    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateDTO passwordUpdateDTO) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        UserEntity userToUpdate = new UserEntity();
        userToUpdate.setId(userId);
        userToUpdate.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        userMapper.updateById(userToUpdate);

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

        List<UserEntity> usersToDelete = userMapper.selectByIds(ids);

        if (usersToDelete.isEmpty()) {
            return;
        }

        for (UserEntity user : usersToDelete) {
            if (AuthUtils.ROLE_SUPER_ADMIN.equals(user.getRole())) {
                String errorMessage = String.format("操作失败：用户 '%s' 是超级管理员，不能被删除", user.getUsername());
                throw new BusinessException(errorMessage);
            }
        }

        userMapper.deleteByIds(ids);
    }

    @Transactional(readOnly = true)
    public UserEntity validateUser(UserLoginDTO loginDTO) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUsername, loginDTO.getUsername());
        UserEntity user = userMapper.selectOne(queryWrapper);

        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return user;
    }
}