package com.zch.toolkits.web.api.version;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import java.lang.reflect.Method;

public class VersionedRequestMapping extends RequestMappingHandlerMapping {
    private final String applicationName;
    // springmvc 5.3开始有这个类
    private final PathPatternParser pathPatternParser = new PathPatternParser();

    public VersionedRequestMapping(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        Class<?> controllerClass = method.getDeclaringClass();
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(controllerClass, ApiVersion.class);
        ApiVersion methodAnnotation = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        // method上的version会覆盖class上面的
        if (methodAnnotation != null) {
            apiVersion = methodAnnotation;
        }
        String[] versions = apiVersion != null ? apiVersion.value() : new String[0];

        if (versions.length > 0) {
            for (String version : versions) {
                String versionedPath = "/" + applicationName + "/" + version;
                // 把version添加到url中
                RequestMappingInfo newMapping = RequestMappingInfo
                        .paths(versionedPath)
                        .options(this.getBuilderConfiguration())
                        .build().combine(mapping);
                super.registerHandlerMethod(handler, method, newMapping);
            }
        } else {
            // 如果没有 ApiVersion 注解，使用原始的 mapping
            super.registerHandlerMethod(handler, method, mapping);
        }
    }

    @Override
    public RequestMappingInfo.BuilderConfiguration getBuilderConfiguration() {
        RequestMappingInfo.BuilderConfiguration config = super.getBuilderConfiguration();
        config.setPatternParser(pathPatternParser);
        return config;
    }
}
