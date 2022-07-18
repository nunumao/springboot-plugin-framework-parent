package com.gitee.starblues.bootstrap.annotation;

import java.lang.annotation.*;

/**
 * 解决方法级别调用时, 当前线程非本插件的ClassLoader注解
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResolveClassLoader {
}
