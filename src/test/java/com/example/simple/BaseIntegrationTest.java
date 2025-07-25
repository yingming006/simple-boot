package com.example.simple;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.GenericContainer; // 引入通用容器
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName; // 引入 DockerImageName

/**
 * 集成测试基类，负责启动和配置 Testcontainers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration-test")
public abstract class BaseIntegrationTest {

    @Container
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withInitScript("schema.sql");

    @Container
    static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        // 动态注入 MySQL 连接信息
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);

        // 动态注入 Redis 连接信息
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }
}