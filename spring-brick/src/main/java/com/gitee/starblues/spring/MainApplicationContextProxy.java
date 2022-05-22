/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.spring;

import com.gitee.starblues.integration.IntegrationConfiguration;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 主程序 ApplicationContext 的实现
 * @author starBlues
 * @version 3.0.1
 */
public class MainApplicationContextProxy extends ApplicationContextProxy implements MainApplicationContext{

    private final GenericApplicationContext applicationContext;
    private final IntegrationConfiguration configuration;
    private final boolean isWebEnvironment;

    public MainApplicationContextProxy(GenericApplicationContext applicationContext,
                                       IntegrationConfiguration configuration) {
        super(applicationContext.getBeanFactory());
        this.applicationContext = applicationContext;
        this.configuration = configuration;
        this.isWebEnvironment = getIsWebEnvironment(applicationContext);
    }

    public MainApplicationContextProxy(GenericApplicationContext applicationContext,
                                       IntegrationConfiguration configuration,
                                       AutoCloseable autoCloseable) {
        super(applicationContext.getBeanFactory(), autoCloseable);
        this.applicationContext = applicationContext;
        this.configuration = configuration;
        this.isWebEnvironment = getIsWebEnvironment(applicationContext);
    }

    @Override
    public Map<String, Map<String, Object>> getConfigurableEnvironment() {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        Map<String, Map<String, Object>> environmentMap = new LinkedHashMap<>(propertySources.size());
        for (PropertySource<?> propertySource : propertySources) {
            if (!(propertySource instanceof EnumerablePropertySource)) {
                continue;
            }
            EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource<?>) propertySource;
            String[] propertyNames = enumerablePropertySource.getPropertyNames();
            Map<String, Object> values = new HashMap<>(propertyNames.length);
            for (String propertyName : propertyNames) {
                values.put(propertyName, enumerablePropertySource.getProperty(propertyName));
            }
            if (!values.isEmpty()) {
                environmentMap.put(propertySource.getName(), values);
            }
        }
        return environmentMap;
    }

    @Override
    public boolean isResolveDependency(String packageName) {
        return packageName.startsWith(configuration.mainPackage());
    }

    @Override
    public boolean isWebEnvironment() {
        return isWebEnvironment;
    }

    private boolean getIsWebEnvironment(GenericApplicationContext applicationContext){
        return applicationContext instanceof AnnotationConfigServletWebServerApplicationContext
                || applicationContext instanceof AnnotationConfigReactiveWebServerApplicationContext;
    }

}
