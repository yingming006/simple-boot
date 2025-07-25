package com.example.simple.modules.role.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("sys_role_permission")
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionEntity {
    private Long roleId;
    private Long permissionId;
}