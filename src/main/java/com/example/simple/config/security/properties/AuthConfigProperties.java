package com.example.simple.config.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthConfigProperties {
    private SingleSession singleSession = new SingleSession();

    @Data
    public static class SingleSession {
        private boolean enabled;
    }
}