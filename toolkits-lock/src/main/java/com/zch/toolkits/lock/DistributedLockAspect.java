package com.zch.toolkits.lock;

import com.zch.toolkits.lock.annotation.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.UUID;

@Aspect
public class DistributedLockAspect {
    // Lua 脚本，用于原子性解锁
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    private static final Logger log = LoggerFactory.getLogger(DistributedLockAspect.class);

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        // 指定 Lua 脚本的位置
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("scripts/unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    private final StringRedisTemplate redisTemplate;
    private final LockKeyGenerator lockKeyGenerator;

    public DistributedLockAspect(StringRedisTemplate redisTemplate, LockKeyGenerator lockKeyGenerator) {
        this.redisTemplate = redisTemplate;
        this.lockKeyGenerator = lockKeyGenerator;
        log.info("DistributedLockAspect initialized successfully");
    }

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        // 1. 解析生成锁的 Key
        String lockKey = "lock:" + lockKeyGenerator.generate(joinPoint, distributedLock);
        // 2. 生成唯一的锁值，用于标识当前线程
        String lockValue = UUID.randomUUID().toString();

        // 3. 尝试加锁
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, distributedLock.expire(), distributedLock.timeUnit());

        if (locked == null || !locked) {
            // 加锁失败，说明有重复请求
            log.warn("Failed to acquire lock, key: {}", lockKey);
            throw new DuplicateException(distributedLock.message());
        }

        log.info("Lock acquired successfully, key: {}, value: {}", lockKey, lockValue);

        try {
            // 4. 加锁成功，执行业务方法
            return joinPoint.proceed();
        } finally {
            // 5. 业务执行完毕，释放锁
            // 使用 Lua 脚本保证原子性，只有 lockValue 匹配时才删除
            try {
                Long result = redisTemplate.execute(
                        UNLOCK_SCRIPT,
                        Collections.singletonList(lockKey),
                        lockValue
                );
                if (result != null && result == 1L) {
                    log.info("Lock released successfully, key: {}, value: {}", lockKey, lockValue);
                } else {
                    // 如果解锁失败，可能是因为锁已过期被自动释放，或者被其他线程错误地修改了
                    log.warn("Failed to release lock or lock has expired automatically, key: {}, value: {}", lockKey, lockValue);
                }
            } catch (Exception e) {
                // 记录解锁异常，但不影响主流程
                log.error("Exception occurred while releasing lock, key: {}", lockKey, e);
            }
        }
    }

}
