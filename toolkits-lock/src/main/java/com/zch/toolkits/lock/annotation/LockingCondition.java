package com.zch.toolkits.lock.annotation;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.redis.core.StringRedisTemplate;

public class LockingCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        try {
            StringRedisTemplate redisTemplate = beanFactory.getBean(StringRedisTemplate.class);
            return redisTemplate != null;
        } catch (Exception e) {
            return false;
        }
    }
}
