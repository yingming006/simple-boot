package com.example.simple.modules.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.simple.modules.permission.entity.PermissionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {
    List<String> selectPermissionCodesByRoleIds(@Param("roleIds") List<Long> roleIds);
}