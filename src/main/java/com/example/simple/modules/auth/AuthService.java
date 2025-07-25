package com.example.simple.modules.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.simple.common.utils.AuthUtils;
import com.example.simple.config.properties.AuthConfigProperties;
import com.example.simple.exception.BusinessException;
import com.example.simple.modules.auth.domain.LoginVO;
import com.example.simple.modules.auth.domain.SysUserTokenEntity;
import com.example.simple.modules.auth.mapper.SysUserTokenMapper;
import com.example.simple.modules.permission.service.PermissionService;
import com.example.simple.modules.user.entity.SysUserEntity;
import com.example.simple.modules.user.mapper.SysUserMapper;
import com.example.simple.security.principal.SecurityPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysUserTokenMapper sysUserTokenMapper;
    private final PermissionService permissionService;
    private final AuthConfigProperties authConfigProperties;

    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 2 * 60 * 60; // 2 hours
    private static final long REFRESH_TOKEN_EXPIRE_SECONDS = 7 * 24 * 60 * 60; // 7 days

    @Transactional
    public LoginVO createToken(SecurityPrincipal principal) {
        if (authConfigProperties.getSingleSession().isEnabled()) {
            invalidateUserTokens(principal.getId());
        }
        LocalDateTime now = LocalDateTime.now();
        String accessToken = UUID.randomUUID().toString().replace("-", "");
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        SysUserTokenEntity sysUserTokenEntity = new SysUserTokenEntity();
        sysUserTokenEntity.setUserId(principal.getId());
        sysUserTokenEntity.setAccessToken(accessToken);
        sysUserTokenEntity.setAccessTokenExpireTime(now.plusSeconds(ACCESS_TOKEN_EXPIRE_SECONDS));
        sysUserTokenEntity.setRefreshToken(refreshToken);
        sysUserTokenEntity.setRefreshTokenExpireTime(now.plusSeconds(REFRESH_TOKEN_EXPIRE_SECONDS));
        sysUserTokenMapper.insert(sysUserTokenEntity);
        return new LoginVO(accessToken, refreshToken);
    }

    @Transactional
    public LoginVO refreshToken(String refreshTokenValue) {
        LambdaQueryWrapper<SysUserTokenEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserTokenEntity::getRefreshToken, refreshTokenValue)
                .eq(SysUserTokenEntity::getIsActive, true)
                .ge(SysUserTokenEntity::getRefreshTokenExpireTime, LocalDateTime.now());
        SysUserTokenEntity oldToken = sysUserTokenMapper.selectOne(queryWrapper);
        if (oldToken == null) {
            throw new BusinessException(401, "刷新凭证无效或已过期");
        }
        SysUserEntity sysUserEntity = sysUserMapper.selectById(oldToken.getUserId());
        if (sysUserEntity == null) {
            oldToken.setIsActive(false);
            sysUserTokenMapper.updateById(oldToken);
            throw new BusinessException(401, "凭证关联的用户不存在");
        }
        oldToken.setIsActive(false);
        sysUserTokenMapper.updateById(oldToken);

        Set<String> permissions = permissionService.getAuthorityStringsByUserId(sysUserEntity.getId());
        SecurityPrincipal principal = new SecurityPrincipal(sysUserEntity.getId(), sysUserEntity.getUsername(), sysUserEntity.getPassword(), permissions);
        return createToken(principal);
    }

    @Transactional
    public void invalidateUserTokens(Long userId) {
        LambdaUpdateWrapper<SysUserTokenEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUserTokenEntity::getUserId, userId)
                .eq(SysUserTokenEntity::getIsActive, true)
                .set(SysUserTokenEntity::getIsActive, false);
        sysUserTokenMapper.update(null, updateWrapper);
    }

    @Transactional(readOnly = true)
    public SecurityPrincipal verifyAccessToken(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new BusinessException(401, "用户未登录，请提供凭证");
        }
        LambdaQueryWrapper<SysUserTokenEntity> tokenQuery = new LambdaQueryWrapper<>();
        tokenQuery.eq(SysUserTokenEntity::getAccessToken, accessToken)
                .eq(SysUserTokenEntity::getIsActive, true)
                .ge(SysUserTokenEntity::getAccessTokenExpireTime, LocalDateTime.now());
        SysUserTokenEntity userToken = sysUserTokenMapper.selectOne(tokenQuery);
        if (userToken == null) {
            throw new BusinessException(401, "凭证无效或已过期");
        }
        SysUserEntity user = sysUserMapper.selectById(userToken.getUserId());
        if (user == null) {
            throw new BusinessException(401, "凭证关联的用户不存在");
        }

        Set<String> permissions = permissionService.getAuthorityStringsByUserId(user.getId());
        return new SecurityPrincipal(user.getId(), user.getUsername(), user.getPassword(), permissions);
    }

    @Transactional
    public void logout() {
        Long currentUserId = AuthUtils.getCurrentUserId();
        if (currentUserId == null) {
            return;
        }
        invalidateUserTokens(currentUserId);
    }
}