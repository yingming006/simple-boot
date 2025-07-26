package com.example.simple.config.web;

import com.example.simple.common.BaseEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * 为所有实现了 BaseEnum 接口的枚举提供通用的 Jackson 序列化器。
 * 使用 @JsonComponent 注解，Spring Boot 会自动将其注册到 ObjectMapper 中。
 */
@JsonComponent
public class BaseEnumJsonComponent {

    /**
     * 静态内部类，实现通用的序列化逻辑。
     */
    public static class Serializer extends JsonSerializer<BaseEnum<?>> {
        @Override
        public void serialize(BaseEnum<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }
            // 将枚举序列化为 { "code": ..., "message": "..." } 格式
            gen.writeStartObject();
            gen.writeObjectField("code", value.getValue());
            gen.writeStringField("message", value.getMessage());
            gen.writeEndObject();
        }
    }
}