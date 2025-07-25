package com.example.simple.config.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.root-path}")
    private String rootPath;

    @Value("${file.access-url-prefix}")
    private String accessUrlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = rootPath.endsWith(File.separator) ? rootPath : rootPath + File.separator;

        registry.addResourceHandler(accessUrlPrefix + "**")
                .addResourceLocations("file:" + path);
    }
}