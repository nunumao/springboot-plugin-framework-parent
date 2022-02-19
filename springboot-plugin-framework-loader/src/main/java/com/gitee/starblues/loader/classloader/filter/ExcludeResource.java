package com.gitee.starblues.loader.classloader.filter;

import java.util.jar.JarEntry;

/**
 * 排除资源
 * @author starBlues
 * @version 3.0.0
 */
@FunctionalInterface
public interface ExcludeResource {

    /**
     * 过滤排除
     * @param jarEntry jarEntry
     * @return boolean
     */
    boolean exclude(JarEntry jarEntry);

}
