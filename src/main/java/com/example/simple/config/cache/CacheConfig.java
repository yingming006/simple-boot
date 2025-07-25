package com.example.simple.config.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置
 * <p>
 * 只有当配置文件中的 app.cache.enabled=true 时，@EnableCaching 才会生效。
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "app.cache.enabled", havingValue = "true", matchIfMissing = true)
public class CacheConfig {
}