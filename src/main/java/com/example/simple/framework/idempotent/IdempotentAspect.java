package com.example.simple.framework.idempotent;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 幂等性校验切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

    private final IdempotentService idempotentService;

    @Pointcut("@annotation(com.example.simple.annotation.Idempotent)")
    public void idempotentPointcut() {
    }

    @Before("idempotentPointcut()")
    public void doBefore() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        idempotentService.checkToken(request);
    }
}