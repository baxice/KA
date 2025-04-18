package com.example.knowledgeapplication.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Around("@annotation(com.example.knowledgeapplication.annotation.OperLog)")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        log.info("Start operation: {}", methodName);

        long startTime = System.currentTimeMillis();
        Object result = pjp.proceed();

        log.info("Operation {} executed in {} ms",
                methodName,
                System.currentTimeMillis() - startTime);
        return result;
    }
}
