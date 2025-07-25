package com.example.simple.modules.permission.converter;

import com.example.simple.modules.permission.entity.PermissionEntity;
import com.example.simple.modules.permission.vo.PermissionVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PermissionConverter {
    PermissionConverter INSTANCE = Mappers.getMapper(PermissionConverter.class);

    PermissionVO toPermissionVO(PermissionEntity entity);
}