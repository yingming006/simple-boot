package com.example.simple.config.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 */
@Configuration
@MapperScan("com.example.simple.modules.**.mapper")
public class MyBatisPlusConfig {

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 使用 ConfigurationCustomizer 为 Mybatis-Plus 添加自定义配置。
     * 这是官方推荐的、用于扩展 Mybatis-Plus 自动配置的、无循环依赖风险的最佳实践。
     * @return ConfigurationCustomizer
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            // 获取类型处理器注册表
            var typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            // 为所有实现了 IEnum 接口的枚举注册默认的处理器
            typeHandlerRegistry.setDefaultEnumTypeHandler(MybatisEnumTypeHandler.class);
        };
    }
}