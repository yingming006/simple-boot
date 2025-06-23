package com.example.simple.config.aop;

import com.example.simple.annotation.RateLimit;
import com.example.simple.common.utils.AuthUtils;
import com.example.simple.exception.BusinessException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    private final LoadingCache<String, RateLimiter> caches = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.DAYS) // 缓存一天后过期
            .build(new CacheLoader<>() {
                @Override
                public RateLimiter load(String key) {
                    double permitsPerSecond = Double.parseDouble(key.split(":")[1]);
                    return RateLimiter.create(permitsPerSecond);
                }
            });

    @Pointcut("@annotation(com.example.simple.annotation.RateLimit)")
    public void rateLimitPointcut() {
    }

    @Around("rateLimitPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        if (rateLimit != null) {
            String key = generateKey(rateLimit, joinPoint);
            double permitsPerSecond = (double) rateLimit.count() / rateLimit.time();

            String cacheKey = "rate_limit:" + key + ":" + permitsPerSecond;

            RateLimiter limiter = caches.get(cacheKey);

            if (!limiter.tryAcquire()) {
                log.debug("触发接口限流，key: {}", key);
                throw new BusinessException(429, rateLimit.message());
            }
        }

        return joinPoint.proceed();
    }

    /**
     * 根据限流类型生成唯一的key
     */
    private String generateKey(RateLimit rateLimit, ProceedingJoinPoint joinPoint) {
        switch (rateLimit.limitType()) {
            case IP:
                return getIpAddress();
            case USER_ID:
                Long userId = AuthUtils.getCurrentUserId();
                if (userId == null) {
                    log.warn("需要按用户ID限流，但用户未登录，回退到IP限流");
                    return getIpAddress();
                }
                return String.valueOf(userId);
            case DEFAULT:
            default:
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                return signature.getDeclaringTypeName() + "." + signature.getName();
        }
    }

    private String getIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}