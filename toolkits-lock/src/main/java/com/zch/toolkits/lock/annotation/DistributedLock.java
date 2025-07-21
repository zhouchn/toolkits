package com.zch.toolkits.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    /**
     * 锁的键 (Key)。支持 SpEL 表达式。
     * 例如: "'user_lock:' + #userId" 或 "'order:' + #order.id"
     */
    String key();

    /**
     * 锁的过期时间。默认为30秒。
     */
    int expire() default 30;

    /**
     * 过期时间的单位。默认为秒。
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 获取锁失败时的提示信息。
     */
    String message() default "Request processing, please do not submit repeatedly";
}