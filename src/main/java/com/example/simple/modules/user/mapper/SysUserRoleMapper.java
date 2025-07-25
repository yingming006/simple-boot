package com.example.simple.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.simple.modules.user.entity.SysUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRoleEntity> {
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关联关系
     */
    void insertBatch(@Param("relations") List<SysUserRoleEntity> relations);
}