package com.example.simple.common;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

/**
 * 通用枚举接口
 * <p>
 * 项目中所有业务枚举都应实现此接口，以便Mybatis-Plus和Jackson进行统一处理。
 *
 * @param <T> code 的类型 (通常为 Integer 或 String)
 */
public interface BaseEnum<T extends Serializable> extends IEnum<T> {

    /**
     * 获取枚举的 code 值，用于数据库存储和前后端交互。
     *
     * @return 枚举的 code
     */
    @Override
    @JsonValue // Jackson序列化时，默认使用此方法的返回值
    T getValue();

    /**
     * 获取枚举的描述信息，主要用于前端展示。
     *
     * @return 枚举的 message
     */
    String getMessage();
}