package com.zch.toolkits.web.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zch.toolkits.web.api.version.VersionedRequestMapping;
import com.zch.toolkits.web.filter.RequestLoggingFilter;
import com.zch.toolkits.web.filter.RequestTracingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer, WebMvcRegistrations {
    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> loggingFilter() {
        RequestLoggingFilter loggingFilter = new RequestLoggingFilter();
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(1024);
        loggingFilter.setIncludeHeaders(false);
        loggingFilter.setAfterMessagePrefix("[REQUEST] ");
        FilterRegistrationBean<RequestLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(loggingFilter);
        registration.addUrlPatterns("/*"); // 指定拦截路径
        registration.setOrder(2); // 设置执行顺序（数值越小优先级越高）
        registration.setName("LoggingFilter"); // 设置 Filter 名称
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RequestTracingFilter> tracingFilter() {
        FilterRegistrationBean<RequestTracingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestTracingFilter());
        registration.addUrlPatterns("/*"); // 指定拦截路径
        registration.setOrder(1); // 设置执行顺序（数值越小优先级越高）
        registration.setName("TracingFilter"); // 设置 Filter 名称
        return registration;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            // 忽略反序列化时未知字段（不会报错）
            builder.failOnUnknownProperties(false);
            // 处理 Java 8 日期时间类型
            builder.modules(new JavaTimeModule());
            // 注册自定义 LocalDateTime -> long 序列化器
            builder.modules(new SimpleModule()
                    .addSerializer(LocalDateTime.class, new LocalDateTimeToLongSerializer())
            );
        };
    }

    static class LocalDateTimeToLongSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value != null) {
                gen.writeNumber(value.toInstant(ZoneOffset.UTC).toEpochMilli()); // 转换为时间戳 (毫秒级)
            }
        }
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new VersionedRequestMapping(applicationName);
    }
}
