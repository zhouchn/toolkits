package com.zch.toolkits.lock;

import com.zch.toolkits.lock.annotation.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class DefaultLockKeyGenerator implements LockKeyGenerator {
    @Override
    public String generate(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        String key = distributedLock.key();
        if (key == null || !key.contains("#")) {
            return key;
        }
        return parseSpelKey(joinPoint, distributedLock.key());
    }

    /**
     * 解析 SpEL 表达式以获取动态的锁 Key
     *
     * @param joinPoint     切点
     * @param keyExpression SpEL 表达式
     * @return 解析后的 Key
     */
    private String parseSpelKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }
}
