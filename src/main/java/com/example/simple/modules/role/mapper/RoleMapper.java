package com.example.simple.modules.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.simple.modules.role.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {
    /**
     * 根据用户ID查询其所有角色的编码
     * @param userId 用户ID
     * @return 角色编码列表 (e.g., ["ADMIN", "USER"])
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}