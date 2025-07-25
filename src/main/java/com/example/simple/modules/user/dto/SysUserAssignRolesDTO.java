package com.example.simple.modules.user.dto;

import lombok.Data;
import java.util.List;

@Data
public class SysUserAssignRolesDTO {
    private List<Long> roleIds;
}