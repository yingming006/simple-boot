package com.example.simple.modules.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.simple.modules.log.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<SysOperationLog> {
}