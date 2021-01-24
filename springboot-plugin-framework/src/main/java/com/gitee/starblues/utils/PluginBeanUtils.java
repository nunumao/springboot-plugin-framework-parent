package com.gitee.starblues.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * 插件bean工具类
 * @author starBlues
 * @version 2.4.0
 */
public class PluginBeanUtils {

    public static <T> List<T> getPluginBeans(ApplicationContext applicationContext, Class<T> aClass) {
        Map<String, T> beansOfTypeMap = applicationContext.getBeansOfType(aClass);
        if(beansOfTypeMap.isEmpty()){
            return Collections.emptyList();
        }
        return new ArrayList<>(beansOfTypeMap.values());
    }

    /**
     * 得到某个接口的实现对象
     * @param sourceObject 遍历的对象
     * @param interfaceClass 接口类类型
     * @return Object
     */
    public static <T> T getObjectByInterfaceClass(Set<Object> sourceObject, Class<T> interfaceClass){
        if(sourceObject == null || sourceObject.isEmpty()){
            return null;
        }
        for (Object configSingletonObject : sourceObject) {
            Set<Class<?>> allInterfacesForClassAsSet = ClassUtils
                    .getAllInterfacesAsSet(configSingletonObject);
            if(allInterfacesForClassAsSet.contains(interfaceClass)){
                return (T) configSingletonObject;
            }
        }
        return null;
    }

    /***
     * 得到存在的bean, 不存在则返回null
     * @param applicationContext ApplicationContext容器
     * @param aClass bean 类型
     * @return 存在bean对象, 不存在返回null
     */
    public static <T> T getExistBean(ApplicationContext applicationContext, Class<T> aClass){
        String[] beanNamesForType = applicationContext.getBeanNamesForType(aClass, false, false);
        if(beanNamesForType.length > 0){
            return applicationContext.getBean(aClass);
        } else {
            return null;
        }
    }

    /***
     * 得到存在的bean, 不存在则返回null
     * @param applicationContext ApplicationContext容器
     * @param beanName bean 名称
     * @return 存在bean对象, 不存在返回null
     */
    public static <T> T getExistBean(ApplicationContext applicationContext, String beanName){
        if(applicationContext.containsBean(beanName)){
            Object bean = applicationContext.getBean(beanName);
            return (T) bean;
        } else {
            return null;
        }
    }

}
