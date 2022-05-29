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

import java.util.Map;

/**
 * 主程序 ApplicationContext 接口
 * @author starBlues
 * @version 3.0.1
 */
public interface MainApplicationContext extends ApplicationContext {

    /**
     * 得到主程序所有配置的 env
     *
     * @return 主程序配置的 env 集合
     */
    Map<String, Map<String, Object>> getConfigurableEnvironment();

    /**
     * 从主程序获取依赖
     *
     * @param requestingBeanName 依赖Bean名称
     * @param dependencyType 依赖类型
     * @return boolean
     */
    Object resolveDependency(String requestingBeanName, Class<?> dependencyType);

    /**
     * 是否为web环境
     * @return boolean
     */
    boolean isWebEnvironment();


}
