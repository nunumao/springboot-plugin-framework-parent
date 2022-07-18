package com.gitee.starblues.loader.launcher.classpath;


import java.net.URL;
import java.util.List;

/**
 * 获取classpath资源路径
 *
 * @author starBlues
 * @version 3.0.4
 * @since 3.0.4
 */
public interface ClasspathResource {

    /**
     * 获取 classpath url 集合
     * @return List
     * @throws Exception 获取异常
     */
    List<URL> getClasspath() throws Exception;

}
