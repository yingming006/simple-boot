package com.example.simple.config.aop;

import com.example.simple.annotation.OperationLog;
import com.example.simple.common.utils.AuthUtils;
import com.example.simple.common.utils.IpUtils;
import com.example.simple.security.principal.SecurityPrincipal;
import com.example.simple.modules.log.domain.SysOperationLog;
import com.example.simple.modules.log.service.OperationLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(com.example.simple.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    @Around("operationLogPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        try {
            handleLog(joinPoint, endTime - startTime);
        } catch (Exception e) {
            log.error("AOP记录操作日志失败", e);
        }

        return result;
    }

    private void handleLog(ProceedingJoinPoint joinPoint, long duration) {
        SysOperationLog logEntity = new SysOperationLog();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLogAnnotation = method.getAnnotation(OperationLog.class);
        logEntity.setOperation(operationLogAnnotation.value());

        SecurityPrincipal loginUser = AuthUtils.getLoginUser();
        if (loginUser != null) {
            logEntity.setUserId(loginUser.getId());
        }

        logEntity.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        logEntity.setParams(serializeParams(joinPoint.getArgs()));
        logEntity.setIpAddress(IpUtils.getCurrentIpAddress());
        logEntity.setDuration(duration);
        operationLogService.save(logEntity);
    }

    /**
     * 序列化参数，并过滤掉不可序列化的类型
     * @param args 方法参数数组
     * @return JSON字符串
     */
    private String serializeParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        try {
            List<Object> filteredArgs = Arrays.stream(args)
                    .filter(arg -> !(arg instanceof HttpServletRequest)
                            && !(arg instanceof HttpServletResponse)
                            && !(arg instanceof MultipartFile)
                            && !(arg instanceof MultipartFile[]))
                    .collect(Collectors.toList());

            return objectMapper.writeValueAsString(filteredArgs);
        } catch (JsonProcessingException e) {
            log.error("操作日志参数序列化失败: {}", e.getMessage());
            return "参数序列化失败";
        }
    }
}