package com.example.simple.modules.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.simple.modules.config.entity.SysConfigEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfigEntity> {
}