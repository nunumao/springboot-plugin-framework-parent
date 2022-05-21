/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.utils.DestroyUtils;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringBeanFactory;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.ReflectionUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ScopeNotActiveException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 插件BeanFactory实现
 * @author starBlues
 * @version 3.0.3
 */
public class PluginListableBeanFactory extends DefaultListableBeanFactory {

    private final Logger logger = LoggerFactory.getLogger(PluginListableBeanFactory.class);

    private final MainApplicationContext applicationContext;

    public PluginListableBeanFactory(MainApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object resolveDependency(DependencyDescriptor descriptor,
                                    @Nullable String requestingBeanName,
                                    @Nullable Set<String> autowiredBeanNames,
                                    @Nullable TypeConverter typeConverter) throws BeansException {
        if(isDisabled(descriptor)){
            return resolveDependencyFromMain(descriptor, false);
        }
        try {
            Object object = super.resolveDependency(descriptor, requestingBeanName, autowiredBeanNames,
                    typeConverter);

            if(object instanceof ObjectProvider){
                return new PluginObjectProviderWrapper((ObjectProvider<Object>) object, descriptor);
            }
            return object;
        } catch (BeansException e){
            if(e instanceof NoSuchBeanDefinitionException){
                Object dependencyBean = resolveDependencyFromMain(descriptor, true);
                if(dependencyBean != null){
                    return dependencyBean;
                }
            }
            throw e;
        }
    }

    @Override
    public void destroySingletons() {
        String[] beanDefinitionNames = getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            destroyBean(beanDefinitionName);
        }
        super.destroySingletons();
        destroyAll();
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
        return super.getBeanProvider(requiredType, allowEagerInit);
    }


    private Object resolveDependencyFromMain(DependencyDescriptor descriptor, boolean isResolveDependency){
        String packageName = descriptor.getDependencyType().getPackage().getName();
        if(isResolveDependency && !applicationContext.isResolveDependency(packageName)){
            return null;
        }
        String dependencyName = descriptor.getDependencyName();
        SpringBeanFactory springBeanFactory = applicationContext.getSpringBeanFactory();
        if(!ObjectUtils.isEmpty(dependencyName) && springBeanFactory.containsBean(dependencyName)){
            return springBeanFactory.getBean(dependencyName);
        } else {
            try {
                return springBeanFactory.getBean(descriptor.getDependencyType());
            } catch (Exception e){
                throw new NoSuchBeanDefinitionException(descriptor.getDependencyType());
            }
        }
    }

    private void destroyAll(){
        ReflectionUtils.findField(this.getClass(), field -> {
            field.setAccessible(true);
            try {
                Object o = field.get(this);
                DestroyUtils.destroyAll(o);
            } catch (IllegalAccessException e) {
                // 忽略
            }
            return false;
        });
    }

    private boolean isDisabled(DependencyDescriptor descriptor){
        String className = descriptor.getDependencyType().getName();
        return PluginDisableAutoConfiguration.isDisabled(className);
    }

    @AllArgsConstructor
    private class PluginObjectProviderWrapper implements ObjectProvider<Object> {

        private final ObjectProvider<Object> pluginObjectProvider;
        private final DependencyDescriptor descriptor;

        @Override
        public Object getObject() throws BeansException {
            if(isDisabled(descriptor)){
                return resolveDependencyFromMain(descriptor, false);
            }
            return pluginObjectProvider.getObject();
        }

        @Override
        public Object getObject(final Object... args) throws BeansException {
            if(isDisabled(descriptor)){
                SpringBeanFactory springBeanFactory = applicationContext.getSpringBeanFactory();
                return springBeanFactory.getBean(descriptor.getDependencyType(), args);
            }
            return pluginObjectProvider.getObject(args);
        }

        @Override
        @Nullable
        public Object getIfAvailable() throws BeansException {
            if(isDisabled(descriptor)){
                try {
                    return getObject();
                } catch (Exception e){
                    return null;
                }
            } else {
                return pluginObjectProvider.getIfAvailable();
            }
        }

        @Override
        public void ifAvailable(Consumer<Object> dependencyConsumer) throws BeansException {
            if(isDisabled(descriptor)){
                Object dependency = getIfAvailable();
                if (dependency != null) {
                    try {
                        dependencyConsumer.accept(dependency);
                    }
                    catch (ScopeNotActiveException ex) {
                        // Ignore
                    }
                }
            } else {
                pluginObjectProvider.ifAvailable(dependencyConsumer);
            }
        }

        @Override
        @Nullable
        public Object getIfUnique() throws BeansException {
            if(isDisabled(descriptor)){
                Object dependency = getIfAvailable();
                if(dependency == null){
                    return Optional.empty();
                } else {
                    return Optional.of(dependency);
                }
            } else {
                return pluginObjectProvider.getIfUnique();
            }
        }

        @Override
        public void ifUnique(Consumer<Object> dependencyConsumer) throws BeansException {
            if(isDisabled(descriptor)){
                Object dependency = getIfUnique();
                if (dependency != null) {
                    try {
                        dependencyConsumer.accept(dependency);
                    } catch (ScopeNotActiveException ex) {
                        // Ignore
                    }
                }
            } else {
               pluginObjectProvider.ifUnique(dependencyConsumer);
            }
        }

        @Override
        public Stream<Object> stream() {
            if(isDisabled(descriptor)){
                return getStreamOfMain();
            } else {
                return pluginObjectProvider.stream();
            }
        }

        @Override
        public Stream<Object> orderedStream() {
            if(isDisabled(descriptor)){
                // TODO 随意排序
                return getStreamOfMain().sorted();
            } else {
                return pluginObjectProvider.orderedStream();
            }
        }

        @SuppressWarnings("unchecked")
        private Stream<Object> getStreamOfMain(){
            SpringBeanFactory springBeanFactory = applicationContext.getSpringBeanFactory();
            Map<String, ?> beansOfType = springBeanFactory.getBeansOfType(descriptor.getDependencyType());
            if(beansOfType.isEmpty()){
                return Stream.empty();
            } else {
                return (Stream<Object>) beansOfType.values().stream();
            }
        }
    }

}
