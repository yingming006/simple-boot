package com.example.simple.modules.user.controller;

import com.example.simple.common.GlobalResponse;
import com.example.simple.common.dto.PageQueryDTO;
import com.example.simple.common.utils.AuthUtils;
import com.example.simple.common.vo.PageVO;
import com.example.simple.modules.user.dto.*;
import com.example.simple.modules.user.service.SysUserService;
import com.example.simple.modules.user.vo.SysUserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/me")
    public GlobalResponse<SysUserVO> getUserInfo() {
        Long userId = AuthUtils.getCurrentUserId();
        SysUserVO sysUserVO = sysUserService.getUserInfo(userId);
        return GlobalResponse.success(sysUserVO);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('users:list')")
    public GlobalResponse<PageVO<SysUserVO>> getUserPage(SysUserDTO queryDTO, PageQueryDTO pageQueryDTO) {
        return GlobalResponse.success(sysUserService.getUserPage(queryDTO, pageQueryDTO));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('users:create')")
    public GlobalResponse<Void> create(@Valid @RequestBody SysUserCreateDTO createDTO) {
        sysUserService.createUser(createDTO);
        return GlobalResponse.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('users:update')")
    public GlobalResponse<Void> updateUser(@PathVariable Long id, @Valid @RequestBody SysUserUpdateDTO updateDTO) {
        sysUserService.updateUser(id, updateDTO);
        return GlobalResponse.success();
    }

    @PutMapping("/me")
    public GlobalResponse<Void> updateProfile(@RequestBody SysUserUpdateDTO updateDTO) {
        Long currentUserId = AuthUtils.getCurrentUserId();
        sysUserService.updateUser(currentUserId, updateDTO);
        return GlobalResponse.success();
    }

    @PutMapping("/me/password")
    public GlobalResponse<Void> updatePassword(@RequestBody SysUserPasswordUpdateDTO passwordUpdateDTO) {
        Long currentUserId = AuthUtils.getCurrentUserId();
        sysUserService.updatePassword(currentUserId, passwordUpdateDTO);
        return GlobalResponse.success();
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('users:delete')")
    public GlobalResponse<Void> delete(@RequestBody List<Long> ids) {
        sysUserService.deleteUsers(ids);
        return GlobalResponse.success();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('users:detail')")
    public GlobalResponse<SysUserVO> getById(@PathVariable Long id) {
        SysUserVO sysUserVO = sysUserService.getUserInfo(id);
        return GlobalResponse.success(sysUserVO);
    }

    /**
     * 获取指定用户的角色ID列表
     */
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('users:detail')")
    public GlobalResponse<List<Long>> getUserRoles(@PathVariable Long id) {
        return GlobalResponse.success(sysUserService.getRoleIdsByUserId(id));
    }

    /**
     * 为指定用户分配角色
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('users:assign_roles')")
    public GlobalResponse<Void> assignRoles(@PathVariable Long id, @RequestBody SysUserAssignRolesDTO dto) {
        sysUserService.assignRoles(id, dto.getRoleIds());
        return GlobalResponse.success();
    }
}