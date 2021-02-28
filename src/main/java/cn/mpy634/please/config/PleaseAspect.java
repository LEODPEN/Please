package cn.mpy634.please.config;

import cn.mpy634.please.Please;
import cn.mpy634.please.lease.Lease;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;

/**
 * @author LEO D PEN
 * @date 2021/2/25
 * @desc
 */
@Slf4j
@Order(44)
@Aspect
@Configuration
public class PleaseAspect {

    @Resource
    private Lease lease;

    @Pointcut("@annotation(cn.mpy634.please.Please)")
    public void leasePointcut() {}

    @Around("leasePointcut()")
    public Object leaseAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("===========> Please progress begin ............");
        Please please = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Please.class);
        return lease.makePlease(
                getKey(joinPoint, please.key()),
                please.waitTimeMs(),
                please.timeoutMs(),
                joinPoint::proceed,
                () -> {throw new RuntimeException(please.failMsg());}
        );
    }

    /**
     * collectionId
     */
    private String getKey(JoinPoint joinPoint, String arg) throws IllegalArgumentException, NoSuchFieldException {
        if (arg == null || arg.trim().isEmpty()) throw new IllegalArgumentException("no arg while Please processing.");
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] argValues = joinPoint.getArgs();
        for (int i = 0; i < argNames.length; i++) {
            if (argNames[i].equals(arg)) {
                return String.valueOf(argValues[i]);
            }
        }
        throw new  NoSuchFieldException("no arg found in args while Please processing.");
    }

}
