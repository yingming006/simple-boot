package com.example.simple.modules.role.controller;

import com.example.simple.common.GlobalResponse;
import com.example.simple.common.dto.PageQueryDTO;
import com.example.simple.common.vo.PageVO;
import com.example.simple.modules.role.dto.RoleAssignPermissionsDTO;
import com.example.simple.modules.role.dto.RoleCreateDTO;
import com.example.simple.modules.role.dto.RoleDTO;
import com.example.simple.modules.role.dto.RoleUpdateDTO;
import com.example.simple.modules.role.service.RoleService;
import com.example.simple.modules.role.vo.RoleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理
 * @author yingm
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 分页查询角色列表
     * @param queryDTO 查询条件
     * @param pageQueryDTO 分页参数
     * @return 角色分页数据
     */
    @GetMapping
    @PreAuthorize("hasAuthority('roles:list')")
    public GlobalResponse<PageVO<RoleVO>> getRolePage(RoleDTO queryDTO, PageQueryDTO pageQueryDTO) {
        return GlobalResponse.success(roleService.getRolePage(queryDTO, pageQueryDTO));
    }

    /**
     * 根据ID获取角色详情
     * @param id 角色ID
     * @return 角色的详细信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:detail')")
    public GlobalResponse<RoleVO> getRoleById(@PathVariable Long id) {
        return GlobalResponse.success(roleService.getRoleById(id));
    }

    /**
     * 创建新角色
     * @param createDTO 新增角色的数据
     * @return 操作结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('roles:create')")
    public GlobalResponse<Void> createRole(@Valid @RequestBody RoleCreateDTO createDTO) {
        roleService.createRole(createDTO);
        return GlobalResponse.success();
    }

    /**
     * 更新角色信息
     * @param id 角色ID
     * @param updateDTO 包含更新信息的数据
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:update')")
    public GlobalResponse<Void> updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateDTO updateDTO) {
        roleService.updateRole(id, updateDTO);
        return GlobalResponse.success();
    }

    /**
     * 批量删除角色
     * @param ids 角色ID列表
     * @return 操作结果
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('roles:delete')")
    public GlobalResponse<Void> deleteRoles(@RequestBody List<Long> ids) {
        roleService.deleteRoles(ids);
        return GlobalResponse.success();
    }

    /**
     * 获取指定角色的权限ID列表
     * @param id 角色ID
     * @return 权限ID列表
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('roles:detail')")
    public GlobalResponse<List<Long>> getRolePermissions(@PathVariable Long id) {
        return GlobalResponse.success(roleService.getPermissionIdsByRoleId(id));
    }

    /**
     * 为指定角色分配权限
     * @param id 角色ID
     * @param dto 包含权限ID列表的数据
     * @return 操作结果
     */
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('roles:assign_permissions')")
    public GlobalResponse<Void> assignPermissions(@PathVariable Long id, @RequestBody RoleAssignPermissionsDTO dto) {
        roleService.assignPermissions(id, dto.getPermissionIds());
        return GlobalResponse.success();
    }
}