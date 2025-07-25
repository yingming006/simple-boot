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

/**
 * 系统用户管理
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 获取当前登录用户的信息
     * @return 当前用户的详细信息
     */
    @GetMapping("/me")
    public GlobalResponse<SysUserVO> getUserInfo() {
        Long userId = AuthUtils.getCurrentUserId();
        SysUserVO userVO = sysUserService.getUserInfo(userId);
        return GlobalResponse.success(userVO);
    }

    /**
     * 分页查询用户列表
     * @param queryDTO 查询条件
     * @param pageQueryDTO 分页参数
     * @return 用户分页数据
     */
    @GetMapping
    @PreAuthorize("hasAuthority('users:list')")
    public GlobalResponse<PageVO<SysUserVO>> getUserPage(SysUserDTO queryDTO, PageQueryDTO pageQueryDTO) {
        return GlobalResponse.success(sysUserService.getUserPage(queryDTO, pageQueryDTO));
    }

    /**
     * 根据ID获取单个用户信息
     * @param id 用户ID
     * @return 用户的详细信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('users:detail')")
    public GlobalResponse<SysUserVO> getById(@PathVariable Long id) {
        SysUserVO userVO = sysUserService.getUserInfo(id);
        return GlobalResponse.success(userVO);
    }

    /**
     * 新增用户
     * @param createDTO 新增用户的数据
     * @return 操作结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('users:create')")
    public GlobalResponse<Void> create(@Valid @RequestBody SysUserCreateDTO createDTO) {
        sysUserService.createUser(createDTO);
        return GlobalResponse.success();
    }

    /**
     * 更新指定用户信息
     * @param id 用户ID
     * @param updateDTO 包含更新信息的数据
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('users:update')")
    public GlobalResponse<Void> updateUser(@PathVariable Long id, @Valid @RequestBody SysUserUpdateDTO updateDTO) {
        sysUserService.updateUser(id, updateDTO);
        return GlobalResponse.success();
    }

    /**
     * 当前登录用户更新自己的个人资料
     * @param updateDTO 包含更新信息的数据
     * @return 操作结果
     */
    @PutMapping("/me")
    public GlobalResponse<Void> updateProfile(@RequestBody SysUserUpdateDTO updateDTO) {
        Long currentUserId = AuthUtils.getCurrentUserId();
        sysUserService.updateUser(currentUserId, updateDTO);
        return GlobalResponse.success();
    }

    /**
     * 当前登录用户修改自己的密码
     * @param passwordUpdateDTO 密码更新数据
     * @return 操作结果
     */
    @PutMapping("/me/password")
    public GlobalResponse<Void> updatePassword(@RequestBody SysUserPasswordUpdateDTO passwordUpdateDTO) {
        Long currentUserId = AuthUtils.getCurrentUserId();
        sysUserService.updatePassword(currentUserId, passwordUpdateDTO);
        return GlobalResponse.success();
    }

    /**
     * 批量删除用户
     * @param ids 用户ID列表
     * @return 操作结果
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('users:delete')")
    public GlobalResponse<Void> delete(@RequestBody List<Long> ids) {
        sysUserService.deleteUsers(ids);
        return GlobalResponse.success();
    }

    /**
     * 获取指定用户的角色ID列表
     * @param id 用户ID
     * @return 角色ID列表
     */
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('users:detail')")
    public GlobalResponse<List<Long>> getUserRoles(@PathVariable Long id) {
        return GlobalResponse.success(sysUserService.getRoleIdsByUserId(id));
    }

    /**
     * 为指定用户分配角色
     * @param id 用户ID
     * @param dto 包含角色ID列表的数据
     * @return 操作结果
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('users:assign_roles')")
    public GlobalResponse<Void> assignRoles(@PathVariable Long id, @RequestBody SysUserAssignRolesDTO dto) {
        sysUserService.assignRoles(id, dto.getRoleIds());
        return GlobalResponse.success();
    }
}