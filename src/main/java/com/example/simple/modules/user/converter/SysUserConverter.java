package com.example.simple.modules.user.converter;

import com.example.simple.modules.user.entity.SysUserEntity;
import com.example.simple.modules.user.dto.SysUserCreateDTO;
import com.example.simple.modules.user.dto.SysUserUpdateDTO;
import com.example.simple.modules.user.vo.SysUserVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SysUserConverter {

    /**
     * 将 User 实体转换为 UserVO 视图对象
     * @param user 实体对象
     * @return 视图对象
     */
    SysUserVO toUserVO(SysUserEntity user);

    /**
     * 将 UserCreateDTO 转换为 User 实体
     * @param createDTO 创建DTO
     * @return 实体对象
     */
    SysUserEntity toUser(SysUserCreateDTO createDTO);

    /**
     * 使用 UserUpdateDTO 更新一个已存在的 User 实体
     * @param updateDTO 更新DTO，包含要修改的字段
     * @param user      数据库中查出的、待更新的实体对象
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void userUpdateDtoToUser(SysUserUpdateDTO updateDTO, @MappingTarget SysUserEntity user);
}