package com.example.simple.config.web;

import com.example.simple.config.properties.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileStorageProperties fileStorageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String rootPath = fileStorageProperties.getRootPath();
        String accessUrlPrefix = fileStorageProperties.getAccessUrlPrefix();

        String path = rootPath.endsWith(File.separator) ? rootPath : rootPath + File.separator;

        registry.addResourceHandler(accessUrlPrefix + "**")
                .addResourceLocations("file:" + path);
    }
}