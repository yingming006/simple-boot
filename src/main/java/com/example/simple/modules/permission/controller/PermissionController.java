package com.example.simple.modules.permission.controller;

import com.example.simple.common.GlobalResponse;
import com.example.simple.modules.permission.service.PermissionService;
import com.example.simple.modules.permission.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 权限管理
 * @author yingm
 */
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 以树形结构获取所有权限
     * @return 权限树
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('permissions:list')")
    public GlobalResponse<List<PermissionVO>> getPermissionTree() {
        return GlobalResponse.success(permissionService.getPermissionTree());
    }
}