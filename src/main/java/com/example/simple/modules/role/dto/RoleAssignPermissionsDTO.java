package com.example.simple.modules.role.dto;

import lombok.Data;
import java.util.List;

@Data
public class RoleAssignPermissionsDTO {
    private List<Long> permissionIds;
}