package com.example.simple.config.doc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class SwaggerUILogger {

    private final Environment environment;

    @EventListener(ApplicationReadyEvent.class)
    public void logSwaggerUiUrl() {
        String port = environment.getProperty("local.server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String swaggerUiPath = environment.getProperty("springdoc.swagger-ui.path", "/swagger-ui.html");
        String apiDocsPath = environment.getProperty("springdoc.api-docs.path", "/v3/api-docs");

        String hostAddress;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
            hostAddress = "localhost";
        }

        String localUrl = String.format("http://localhost:%s%s%s", port, contextPath, swaggerUiPath);
        String networkUrl = String.format("http://%s:%s%s%s", hostAddress, port, contextPath, swaggerUiPath);
        String apiDocsUrl = String.format("http://localhost:%s%s%s", port, contextPath, apiDocsPath);

        log.info("----------------------------------------------------------");
        log.info("Application '{}' is running! Access URLs:", environment.getProperty("spring.application.name"));
        log.info("  Local Swagger UI: \t{}", localUrl);
        log.info("  External Swagger UI:\t{}", networkUrl);
        log.info("  API Doc JSON: \t\t{}", apiDocsUrl);
        log.info("  Profile(s): \t\t{}", String.join(",", environment.getActiveProfiles()));
        log.info("----------------------------------------------------------");
    }
}