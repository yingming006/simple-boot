package com.example.simple.modules.dict.converter;

import com.example.simple.modules.dict.dto.SysDictDataCreateDTO;
import com.example.simple.modules.dict.dto.SysDictDataUpdateDTO;
import com.example.simple.modules.dict.entity.SysDictDataEntity;
import com.example.simple.modules.dict.vo.SysDictDataVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SysDictDataConverter {
    SysDictDataConverter INSTANCE = Mappers.getMapper(SysDictDataConverter.class);

    SysDictDataVO toVO(SysDictDataEntity entity);
    SysDictDataEntity fromCreateDTO(SysDictDataCreateDTO dto);
    void updateFromDTO(SysDictDataUpdateDTO dto, @MappingTarget SysDictDataEntity entity);
}