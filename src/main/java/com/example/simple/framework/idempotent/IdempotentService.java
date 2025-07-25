package com.example.simple.framework.idempotent;

import com.example.simple.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性服务，负责Token的生成与校验
 */
@Service
@RequiredArgsConstructor
public class IdempotentService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String IDEMPOTENT_TOKEN_HEADER = "Idempotent-Token";
    private static final String IDEMPOTENT_TOKEN_PREFIX = "idempotent:token:";

    /**
     * 创建并返回一个幂等Token。
     * @return Token 字符串
     */
    public String createToken() {
        String token = UUID.randomUUID().toString();
        String key = IDEMPOTENT_TOKEN_PREFIX + token;
        // 将 Token 存入 Redis，有效期30分钟
        redisTemplate.opsForValue().set(key, "1", 30, TimeUnit.MINUTES);
        return token;
    }

    /**
     * 校验请求中的幂等Token。
     * @param request HTTP 请求
     */
    public void checkToken(HttpServletRequest request) {
        String token = request.getHeader(IDEMPOTENT_TOKEN_HEADER);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("缺少幂等性Token");
        }

        String key = IDEMPOTENT_TOKEN_PREFIX + token;
        // 尝试从 Redis 中删除这个 key。
        // delete 是原子操作，多个并发请求只有一个能成功删除。
        Boolean deleted = redisTemplate.delete(key);

        // 如果删除失败（deleted 为 null 或 false），说明 key 不存在，是重复请求
        if (deleted == null || !deleted) {
            throw new BusinessException("请勿重复提交");
        }
    }
}