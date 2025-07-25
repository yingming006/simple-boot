package com.example.simple.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;

import java.util.List;

/**
 * MySQL 代码生成
 *
 * @author lanjerry
 * @since 3.5.3
 */
public class MySQLGeneratorTest {

    static final String DB_URL = "jdbc:mysql://localhost:3306/simple_api_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
    static final String DB_USERNAME = "root";
    static final String DB_PASSWORD = "root";
    // 表名
    static final List<String> TABLE_NAME = List.of("sys_role");
    // 表前缀
    static final List<String> TABLE_PREFIX = List.of("sys_");
    // 模块名
    static final String MODULE_NAME = "role";
    // 父包名
    static final String PARENT_PACKAGE = "com.example.simple.modules";
    // 输出目录
    static final String OUTPUT_DIR = "D://mp-generated-code";
    // 作者
    static final String AUTHOR = "";

    public static void main(String[] args) {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.global(globalConfig().build());
        generator.packageInfo(packageConfig().build());
        generator.injection(injectionConfig().build());
        generator.execute();
        System.out.println("代码生成完毕，输出目录：" + OUTPUT_DIR);
    }

    /**
     * 数据源配置
     */
    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig
            .Builder(DB_URL, DB_USERNAME, DB_PASSWORD)
            .build();

    /**
     * 全局配置
     */
    private static GlobalConfig.Builder globalConfig() {
        return new GlobalConfig.Builder()
                .outputDir(OUTPUT_DIR)
                .disableOpenDir()
                .author(AUTHOR)
                .dateType(DateType.TIME_PACK);
    }

    /**
     * 包配置
     */
    protected static PackageConfig.Builder packageConfig() {
        return new PackageConfig.Builder()
                .parent(PARENT_PACKAGE)
                .moduleName(MODULE_NAME)
                .service("service")
                .serviceImpl("service");
    }

    /**
     * 策略配置
     */
    private static StrategyConfig.Builder strategyConfig() {
        StrategyConfig.Builder builder = new StrategyConfig.Builder();
        builder.addInclude(TABLE_NAME)
                .addTablePrefix(TABLE_PREFIX);
        // Entity 策略
        builder.entityBuilder()
                .enableFileOverride()
                .enableLombok()
                .enableSerialAnnotation()
                .idType(IdType.ASSIGN_ID)
                .javaTemplate("/templates/entity.java.vm");

        // Controller 策略
        builder.controllerBuilder()
                .enableFileOverride()
                .enableRestStyle()
                .template("/templates/controller.java.vm");

        // Service 策略
        builder.serviceBuilder()
                .enableFileOverride()
                .disableService()
                .formatServiceImplFileName("%sService")
                .serviceImplTemplate("/templates/service.java.vm");

        // Mapper 策略
        builder.mapperBuilder()
                .enableFileOverride()
                .enableBaseResultMap()
                .mapperTemplate("/templates/mapper.java.vm")
                .mapperXmlTemplate("/templates/mapper.xml.vm");

        return builder;
    }

    /**
     * 注入配置
     */
    protected static InjectionConfig.Builder injectionConfig() {

        InjectionConfig.Builder configBuilder = new InjectionConfig.Builder();

        configBuilder.customFile(builder -> builder.fileName("VO.java")
                .enableFileOverride()
                .templatePath("/templates/vo.java.vm")
                .packageName("vo"))
                .customFile(builder -> builder.fileName("DTO.java")
                        .enableFileOverride()
                        .templatePath("/templates/dto.java.vm")
                        .packageName("dto"))
                .customFile(builder -> builder.fileName("Converter.java")
                        .enableFileOverride()
                        .templatePath("/templates/converter.java.vm")
                        .packageName("converter"))
        ;

        configBuilder.beforeOutputFile((tableInfo, objectMap) -> {
            System.out.println("==================================================");
            System.out.println("正在为表 [" + tableInfo.getName() + "] 生成文件...");
            System.out.println("模板变量 (objectMap) 内容如下：");
            objectMap.forEach((key, value) -> {
                String valueStr = (value != null) ? value.toString() : "null";
                if (valueStr.length() > 200) {
                    valueStr = valueStr.substring(0, 197) + "...";
                }
                System.out.println("  key: " + key + ", value: " + valueStr);
            });
            System.out.println("==================================================");
        });
        return configBuilder;
    }
}