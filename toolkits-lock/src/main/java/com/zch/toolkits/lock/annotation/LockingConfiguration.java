package com.zch.toolkits.lock.annotation;

import com.zch.toolkits.lock.DefaultLockKeyGenerator;
import com.zch.toolkits.lock.DistributedLockAspect;
import com.zch.toolkits.lock.LockKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration(proxyBeanMethods = false)
@DependsOn(value = {"redisTemplate", "stringRedisTemplate"})
public class LockingConfiguration {

    @Bean
    public DistributedLockAspect distributedLockAspect(StringRedisTemplate redisTemplate) {
        LockKeyGenerator keyGenerator = new DefaultLockKeyGenerator();
        return new DistributedLockAspect(redisTemplate, keyGenerator);
    }
}
