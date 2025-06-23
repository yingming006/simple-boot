package com.example.simple.modules.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.simple.common.utils.AuthUtils;
import com.example.simple.config.properties.AuthConfigProperties;
import com.example.simple.exception.BusinessException;
import com.example.simple.interceptor.LoginUser;
import com.example.simple.modules.auth.domain.UserToken;
import com.example.simple.modules.auth.domain.LoginVO;
import com.example.simple.modules.auth.mapper.UserTokenMapper;
import com.example.simple.modules.user.domain.User;
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
    public LoginVO createToken(User user) {
        if (authConfigProperties.getSingleSession().isEnabled()) {
            invalidateUserTokens(user.getId());
        }

        LocalDateTime now = LocalDateTime.now();
        String accessToken = UUID.randomUUID().toString().replace("-", "");
        String refreshToken = UUID.randomUUID().toString().replace("-", "");

        UserToken userToken = new UserToken();
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
        LambdaQueryWrapper<UserToken> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserToken::getRefreshToken, refreshTokenValue)
                .eq(UserToken::getIsActive, true)
                .ge(UserToken::getRefreshTokenExpireTime, LocalDateTime.now());

        UserToken oldToken = userTokenMapper.selectOne(queryWrapper);
        if (oldToken == null) {
            throw new BusinessException(401, "刷新凭证无效或已过期");
        }

        oldToken.setIsActive(false);
        userTokenMapper.updateById(oldToken);

        User user = new User();
        user.setId(oldToken.getUserId());
        return createToken(user);
    }

    @Transactional
    public void invalidateUserTokens(Long userId) {
        LambdaUpdateWrapper<UserToken> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserToken::getUserId, userId)
                .eq(UserToken::getIsActive, true)
                .set(UserToken::getIsActive, false);
        userTokenMapper.update(null, updateWrapper);
    }

    @Transactional(readOnly = true)
    public LoginUser verifyAccessToken(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new BusinessException(401, "用户未登录，请提供凭证");
        }

        LambdaQueryWrapper<UserToken> tokenQuery = new LambdaQueryWrapper<>();
        tokenQuery.eq(UserToken::getAccessToken, accessToken)
                .eq(UserToken::getIsActive, true)
                .ge(UserToken::getAccessTokenExpireTime, LocalDateTime.now());

        UserToken userToken = userTokenMapper.selectOne(tokenQuery);
        if (userToken == null) {
            throw new BusinessException(401, "凭证无效或已过期");
        }

        User user = userMapper.selectById(userToken.getUserId());
        if (user == null) {
            // Token有效但用户被删了，也算认证失败
            throw new BusinessException(401, "凭证关联的用户不存在");
        }

        return new LoginUser(user.getId(), user.getRole());
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