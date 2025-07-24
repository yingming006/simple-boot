package com.example.simple.modules.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.simple.common.utils.AuthUtils;
import com.example.simple.config.properties.AuthConfigProperties;
import com.example.simple.exception.BusinessException;
import com.example.simple.interceptor.LoginUser;
import com.example.simple.modules.auth.domain.UserTokenEntity;
import com.example.simple.modules.auth.domain.LoginVO;
import com.example.simple.modules.auth.mapper.UserTokenMapper;
import com.example.simple.modules.user.entity.UserEntity;
import com.example.simple.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final UserTokenMapper userTokenMapper;
    private final AuthConfigProperties authConfigProperties;

    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 2 * 60 * 60; // 2 hours
    private static final long REFRESH_TOKEN_EXPIRE_SECONDS = 7 * 24 * 60 * 60; // 7 days

    @Transactional
    public LoginVO createToken(LoginUser user) {
        if (authConfigProperties.getSingleSession().isEnabled()) {
            invalidateUserTokens(user.getId());
        }

        LocalDateTime now = LocalDateTime.now();
        String accessToken = UUID.randomUUID().toString().replace("-", "");
        String refreshToken = UUID.randomUUID().toString().replace("-", "");

        UserTokenEntity userToken = new UserTokenEntity();
        userToken.setUserId(user.getId());
        userToken.setAccessToken(accessToken);
        userToken.setAccessTokenExpireTime(now.plusSeconds(ACCESS_TOKEN_EXPIRE_SECONDS));
        userToken.setRefreshToken(refreshToken);
        userToken.setRefreshTokenExpireTime(now.plusSeconds(REFRESH_TOKEN_EXPIRE_SECONDS));
        userTokenMapper.insert(userToken);

        return new LoginVO(accessToken, refreshToken);
    }

    @Transactional
    public LoginVO refreshToken(String refreshTokenValue) {
        LambdaQueryWrapper<UserTokenEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTokenEntity::getRefreshToken, refreshTokenValue)
                .eq(UserTokenEntity::getIsActive, true)
                .ge(UserTokenEntity::getRefreshTokenExpireTime, LocalDateTime.now());

        UserTokenEntity oldToken = userTokenMapper.selectOne(queryWrapper);
        if (oldToken == null) {
            throw new BusinessException(401, "刷新凭证无效或已过期");
        }

        UserEntity userEntity = userMapper.selectById(oldToken.getUserId());
        if (userEntity == null) {
            oldToken.setIsActive(false);
            userTokenMapper.updateById(oldToken);
            throw new BusinessException(401, "凭证关联的用户不存在");
        }

        oldToken.setIsActive(false);
        userTokenMapper.updateById(oldToken);

        LoginUser loginUser = new LoginUser(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword(), userEntity.getRole());

        return createToken(loginUser);
    }

    @Transactional
    public void invalidateUserTokens(Long userId) {
        LambdaUpdateWrapper<UserTokenEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserTokenEntity::getUserId, userId)
                .eq(UserTokenEntity::getIsActive, true)
                .set(UserTokenEntity::getIsActive, false);
        userTokenMapper.update(null, updateWrapper);
    }

    @Transactional(readOnly = true)
    public LoginUser verifyAccessToken(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new BusinessException(401, "用户未登录，请提供凭证");
        }

        LambdaQueryWrapper<UserTokenEntity> tokenQuery = new LambdaQueryWrapper<>();
        tokenQuery.eq(UserTokenEntity::getAccessToken, accessToken)
                .eq(UserTokenEntity::getIsActive, true)
                .ge(UserTokenEntity::getAccessTokenExpireTime, LocalDateTime.now());

        UserTokenEntity userToken = userTokenMapper.selectOne(tokenQuery);
        if (userToken == null) {
            throw new BusinessException(401, "凭证无效或已过期");
        }

        UserEntity user = userMapper.selectById(userToken.getUserId());
        if (user == null) {
            throw new BusinessException(401, "凭证关联的用户不存在");
        }

        return new LoginUser(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
    }

    @Transactional
    public void logout() {
        Long currentUserId = AuthUtils.getCurrentUserId();
        if (currentUserId == null) {
            // 如果用户本就未登录（比如token已过期），则无需任何操作
            return;
        }
        // 直接调用已有的方法使该用户的所有Token失效
        invalidateUserTokens(currentUserId);
    }
}