package com.delivery.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LoggingAspect {
    
    @Around("within(com.delivery..*)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        StopWatch stopWatch = new StopWatch();
        
        logger.debug("Starting method: {}", methodName);
        stopWatch.start();
        
        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            logger.debug("Method {} completed in {}ms", methodName, stopWatch.getTotalTimeMillis());
            return result;
        } catch (Exception e) {
            logger.error("Method {} failed with exception: {}", methodName, e.getMessage(), e);
            throw e;
        }
    }
}
