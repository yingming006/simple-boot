package com.example.simple.config.aop;

import com.example.simple.annotation.RequiresRole;
import com.example.simple.common.utils.AuthUtils;
import com.example.simple.exception.BusinessException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Aspect
@Component
public class RoleAspect {

    @Before("@annotation(requiresRole)")
    public void checkRole(RequiresRole requiresRole) {
        String requiredRole = requiresRole.value();
        if (!StringUtils.hasText(requiredRole)) {
            return;
        }

        String currentUserRole = AuthUtils.getUserRole();
        if (!requiredRole.equals(currentUserRole)) {
            throw new BusinessException(403, "无权限访问！");
        }
    }
}