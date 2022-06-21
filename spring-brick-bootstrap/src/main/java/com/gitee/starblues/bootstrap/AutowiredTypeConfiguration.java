package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 设置AutowiredType到AutowiredAnnotationBeanPostProcessor
 * @author gongguanpeng
 * @version 3.0.3
 */
@Configuration
public class AutowiredTypeConfiguration {
    @Bean
    public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor(){
        AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        autowiredAnnotationBeanPostProcessor.setAutowiredAnnotationType(AutowiredType.class);
        return autowiredAnnotationBeanPostProcessor;
    }
}
