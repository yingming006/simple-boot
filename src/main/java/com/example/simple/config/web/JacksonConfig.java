package com.example.simple.config.web;

import com.example.simple.common.BaseEnum;
import com.example.simple.config.web.xss.XssDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 配置 Jackson ObjectMapper
     * 1. 统一日期时间格式
     * 2. Long/BigInteger 序列化为字符串，防止前端精度丢失
     * 3. 增加自定义的 XSS 过滤反序列化器
     * 4. 增加通用的枚举序列化处理器
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer(XssDeserializer xssDeserializer) {
        return builder -> {
            builder.timeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);

            // 日期时间格式化
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            builder.serializerByType(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_FORMAT)));
            builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_FORMAT)));

            // Long/BigInteger 转字符串
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            builder.serializerByType(BigInteger.class, ToStringSerializer.instance);

            // 1. XSS 过滤模块
            SimpleModule xssModule = new SimpleModule("XssStringJsonDeserializer");
            xssModule.addDeserializer(String.class, xssDeserializer);

            // 2. 通用枚举处理模块
            SimpleModule enumModule = new SimpleModule("BaseEnumModule");
            enumModule.addSerializer(BaseEnum.class, new BaseEnumSerializer());

            builder.modules(xssModule, enumModule);
        };
    }

    /**
     * 通用枚举序列化器
     * 将所有实现 BaseEnum 接口的枚举序列化为包含 code 和 message 的 JSON 对象。
     */
    public static class BaseEnumSerializer extends JsonSerializer<BaseEnum<?>> {
        @Override
        public void serialize(BaseEnum<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }
            gen.writeStartObject();
            gen.writeObjectField("code", value.getValue());
            gen.writeStringField("message", value.getMessage());
            gen.writeEndObject();
        }
    }
}