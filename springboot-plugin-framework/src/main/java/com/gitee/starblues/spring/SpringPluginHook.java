package com.gitee.starblues.spring;

/**
 * @author starBlues
 * @version 1.0
 */
public interface SpringPluginHook extends AutoCloseable{

    ApplicationContext getApplicationContext();

}
