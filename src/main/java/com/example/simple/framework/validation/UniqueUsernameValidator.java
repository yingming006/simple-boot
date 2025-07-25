package com.example.simple.framework.validation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.simple.framework.validation.annotation.UniqueUsername;
import com.example.simple.modules.user.entity.SysUserEntity;
import com.example.simple.modules.user.mapper.SysUserMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @UniqueUsername 注解的校验器实现。
 * 负责查询数据库以验证用户名的唯一性。
 */
@Component
@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    private final SysUserMapper sysUserMapper;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        // 如果传入的 username 为空或null，我们不在这里处理，
        // 应该由 @NotBlank 等其他注解来处理。
        if (!StringUtils.hasText(username)) {
            return true;
        }

        // 查询数据库中是否存在具有相同用户名的记录
        LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserEntity::getUsername, username);

        // 如果查询到的记录数大于0，说明用户名已存在，校验失败
        return sysUserMapper.selectCount(queryWrapper) == 0;
    }
}