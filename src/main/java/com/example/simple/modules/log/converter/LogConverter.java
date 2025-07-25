package com.example.simple.modules.log.converter;

import com.example.simple.modules.log.entity.SysOperationLog;
import com.example.simple.modules.log.vo.LogVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LogConverter {
    LogConverter INSTANCE = Mappers.getMapper(LogConverter.class);

    LogVO toLogVO(SysOperationLog entity);
}