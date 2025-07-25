package com.example.simple.config.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * MyBatis-Plus 配置类
 */
@Configuration
@MapperScan("com.example.simple.modules.**.mapper")
public class MyBatisPlusConfig implements InitializingBean {

    private final List<SqlSessionFactory> sqlSessionFactoryList;

    public MyBatisPlusConfig(List<SqlSessionFactory> sqlSessionFactoryList) {
        this.sqlSessionFactoryList = sqlSessionFactoryList;
    }

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
     * 在 Spring 容器初始化完成后，为 Mybatis-Plus 配置全局的枚举类型处理器。
     */
    @Override
    public void afterPropertiesSet() {
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
            // setDefaultEnumTypeHandler 会自动处理所有实现了 IEnum 接口的枚举
            // 但为了更明确地指向我们的业务枚举，可以指定扫描的包
            typeHandlerRegistry.setDefaultEnumTypeHandler(com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler.class);
        }
    }
}