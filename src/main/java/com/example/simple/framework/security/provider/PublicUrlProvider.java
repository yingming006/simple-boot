package com.example.simple.framework.security.provider;

import com.example.simple.annotation.AuthIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier; // 引入 Qualifier
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 在项目启动时，扫描所有被 @AuthIgnore 标记的URL，并提供给Spring Security配置使用。
 */
@Component
public class PublicUrlProvider {

    private final Set<String> publicUrls = new HashSet<>();

    @Autowired
    public PublicUrlProvider(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {

        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();

            boolean isPublic = handlerMethod.hasMethodAnnotation(AuthIgnore.class) ||
                    handlerMethod.getBeanType().isAnnotationPresent(AuthIgnore.class);

            if (isPublic) {
                RequestMappingInfo mappingInfo = entry.getKey();
                if (mappingInfo.getPathPatternsCondition() != null) {
                    publicUrls.addAll(mappingInfo.getPathPatternsCondition().getPatternValues());
                }
            }
        }
    }

    /**
     * 获取所有公开访问的URL路径数组。
     * @return URL路径数组
     */
    public String[] getPublicUrls() {
        return publicUrls.toArray(new String[0]);
    }
}