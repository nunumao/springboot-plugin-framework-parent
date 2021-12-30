package com.gitee.starblues.integration.listener;

import org.springframework.context.ApplicationContext;

/**
 * 默认的初始化监听者。内置注册
 *
 * @author starBlues
 * @version 1.0
 */
public class DefaultInitializerListener implements PluginInitializerListener{

    public final ApplicationContext applicationContext;

    public DefaultInitializerListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void before() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void failure(Throwable throwable) {

    }
}
