package com.example.simple.framework.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 在应用启动完成后，强制检查数据库连接是否可用。
 * 如果连接失败，将抛出异常并中止应用启动。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseHealthChecker implements ApplicationRunner {

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Performing database connection health check...");
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                log.info("Database connection is healthy.");
            } else {
                throw new RuntimeException("Database connection validation failed.");
            }
        } catch (Exception e) {
            log.error("Database connection health check failed. Application will not start.", e);
            // 重新抛出异常，这将使 Spring Boot 启动失败
            throw e;
        }
    }
}