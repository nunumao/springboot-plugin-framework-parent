package com.gitee.starblues.bootstrap.coexist;

import com.gitee.starblues.bootstrap.annotation.ResolveClassLoader;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Coexist模式下解决当前调用现场非本插件的ClassLoader的切面
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
@Aspect
public class CoexistResolveClassLoaderAspect {

    @Pointcut("@annotation(com.gitee.starblues.bootstrap.annotation.ResolveClassLoader)")
    public void test() {

    }

    @Around("@annotation(resolveClassLoader)")
    public Object around(ProceedingJoinPoint pjp, ResolveClassLoader resolveClassLoader) throws Throwable{
        Thread thread = Thread.currentThread();
        ClassLoader oldClassLoader = thread.getContextClassLoader();
        try {
            Object target = pjp.getTarget();
            thread.setContextClassLoader(target.getClass().getClassLoader());
            return pjp.proceed();
        } finally {
            thread.setContextClassLoader(oldClassLoader);
        }
    }

}
