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

import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringBeanFactory;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 空的MainApplicationContext实现
 * @author starBlues
 * @version 3.0.3
 */
public class EmptyMainApplicationContext implements MainApplicationContext {

    private final SpringBeanFactory springBeanFactory = new EmptySpringBeanFactory();

    @Override
    public SpringBeanFactory getSpringBeanFactory() {
        return springBeanFactory;
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public Map<String, Map<String, Object>> getConfigurableEnvironment() {
        return Collections.emptyMap();
    }

    @Override
    public Object resolveDependency(String requestingBeanName, Class<?> dependencyType) {
        return null;
    }

    @Override
    public boolean isWebEnvironment() {
        return false;
    }

}
