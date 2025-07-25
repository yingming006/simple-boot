package com.example.simple.modules.dict.converter;

import com.example.simple.modules.dict.dto.SysDictTypeCreateDTO;
import com.example.simple.modules.dict.dto.SysDictTypeUpdateDTO;
import com.example.simple.modules.dict.entity.SysDictTypeEntity;
import com.example.simple.modules.dict.vo.SysDictTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SysDictTypeConverter {
    SysDictTypeConverter INSTANCE = Mappers.getMapper(SysDictTypeConverter.class);

    SysDictTypeVO toVO(SysDictTypeEntity entity);
    SysDictTypeEntity fromCreateDTO(SysDictTypeCreateDTO dto);
    void updateFromDTO(SysDictTypeUpdateDTO dto, @MappingTarget SysDictTypeEntity entity);
}