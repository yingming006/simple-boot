package com.example.simple.config.doc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

/**
 * 在应用启动完成后，打印 Actuator 端点的访问地址到控制台。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ActuatorLogger {

    private final Environment environment;

    @EventListener(ApplicationReadyEvent.class)
    public void logActuatorUrls() {
        // 检查 Actuator web 端点是否被暴露
        String exposure = environment.getProperty("management.endpoints.web.exposure.include");
        if (exposure == null || exposure.trim().isEmpty() || exposure.equals("\"\"")) {
            return; // 如果没有暴露任何端点，则不打印日志
        }

        String port = environment.getProperty("local.server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String actuatorBasePath = environment.getProperty("management.endpoints.web.base-path", "/actuator");

        // 兼容 context-path 为 "/" 或为空的情况
        if ("/".equals(contextPath)) {
            contextPath = "";
        }

        String localHealthUrl = String.format("http://localhost:%s%s%s/health", port, contextPath, actuatorBasePath);

        log.info("----------------------------------------------------------");
        log.info("Actuator Endpoints are enabled! Access URLs:");
        log.info("  Health Check: \t{}", localHealthUrl);

        // 如果暴露了更多端点，也可以在这里添加打印逻辑
        if (exposure.contains("info")) {
            String localInfoUrl = String.format("http://localhost:%s%s%s/info", port, contextPath, actuatorBasePath);
            log.info("  Application Info: \t{}", localInfoUrl);
        }
        if (exposure.contains("metrics")) {
            String localMetricsUrl = String.format("http://localhost:%s%s%s/metrics", port, contextPath, actuatorBasePath);
            log.info("  Metrics: \t\t{}", localMetricsUrl);
        }

        log.info("----------------------------------------------------------");
    }
}