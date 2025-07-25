package com.example.simple.modules.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.simple.modules.role.entity.RolePermissionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermissionEntity> {
    /**
     * 批量插入角色权限关联关系
     */
    void insertBatch(@Param("relations") List<RolePermissionEntity> relations);
}