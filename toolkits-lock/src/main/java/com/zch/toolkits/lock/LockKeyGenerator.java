package com.zch.toolkits.lock;

import com.zch.toolkits.lock.annotation.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 分布式锁键生成器接口，用于为分布式锁生成唯一的键名。
 *
 * <p>实现类需要根据方法调用上下文和锁注解配置，生成用于Redis分布式锁的唯一键。
 * 默认实现{@link DefaultLockKeyGenerator}支持SpEL表达式解析，可以从方法参数中动态生成键。</p>
 *
 * <p>该接口主要用于{@link DistributedLockAspect}切面中，在方法执行前后实现加锁/解锁逻辑。</p>
 */
public interface LockKeyGenerator {
    /**
     * 生成分布式锁的唯一键
     *
     * @param joinPoint       切点对象，包含被拦截方法的信息
     * @param distributedLock 分布式锁注解，包含锁的配置信息
     * @return 生成的锁键字符串
     */
    String generate(ProceedingJoinPoint joinPoint, DistributedLock distributedLock);
}
