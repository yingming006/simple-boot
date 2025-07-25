package com.example.simple.modules.config.converter;

import com.example.simple.modules.config.entity.SysConfigEntity;
import com.example.simple.modules.config.vo.SysConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SysConfigConverter {
    SysConfigConverter INSTANCE = Mappers.getMapper(SysConfigConverter.class);

    SysConfigVO toVO(SysConfigEntity entity);
}