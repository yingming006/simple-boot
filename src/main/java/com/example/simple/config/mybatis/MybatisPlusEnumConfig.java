package com.example.simple.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * Mybatis-Plus 枚举处理器的后置配置。
 * <p>
 * 通过监听 ContextRefreshedEvent，确保在所有Bean都初始化完毕后才进行配置，
 */
@Configuration
@RequiredArgsConstructor
public class MybatisPlusEnumConfig implements ApplicationListener<ContextRefreshedEvent> {

    private final List<SqlSessionFactory> sqlSessionFactoryList;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
            typeHandlerRegistry.setDefaultEnumTypeHandler(MybatisEnumTypeHandler.class);
        }
    }
}