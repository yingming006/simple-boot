package com.example.simple.modules.role.converter;

import com.example.simple.modules.role.dto.RoleCreateDTO;
import com.example.simple.modules.role.dto.RoleUpdateDTO;
import com.example.simple.modules.role.entity.RoleEntity;
import com.example.simple.modules.role.vo.RoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleConverter {
    RoleConverter INSTANCE = Mappers.getMapper(RoleConverter.class);

    RoleVO toRoleVO(RoleEntity entity);

    RoleEntity toRole(RoleCreateDTO createDTO);

    void updateRoleFromDto(RoleUpdateDTO updateDTO, @MappingTarget RoleEntity entity);
}