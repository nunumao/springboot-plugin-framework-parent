package com.gitee.starblues.integration.simple;

import com.gitee.starblues.core.DefaultPluginManager;
import com.gitee.starblues.core.PluginManager;
import com.gitee.starblues.loader.classloader.GeneralUrlClassLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class SimpleDevelopmentModeInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        GeneralUrlClassLoader classLoader = (GeneralUrlClassLoader)Thread.currentThread().getContextClassLoader();
        try {
            classLoader.addUrl("D:\\etc\\kitte\\ksm\\springboot-plugin-framework-parent\\springboot-plugin-framework-example\\example-plugins-basic\\example-basic-1\\target\\classes");
            classLoader.addUrl("D:\\etc\\kitte\\ksm\\springboot-plugin-framework-parent\\spring-brick-bootstrap\\target\\classes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
