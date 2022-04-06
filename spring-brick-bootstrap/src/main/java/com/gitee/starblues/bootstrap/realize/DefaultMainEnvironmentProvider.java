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

package com.gitee.starblues.bootstrap.realize;

import com.gitee.starblues.loader.utils.ObjectUtils;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.utils.MapValueGetter;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * 主程序配置信息提供者默认实现
 *
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultMainEnvironmentProvider implements MainEnvironmentProvider{

    private final MainApplicationContext mainApplicationContext;

    public DefaultMainEnvironmentProvider(MainApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
    }

    @Override
    public Object getValue(String name) {
        Map<String, Map<String, Object>> configurableEnvironment = mainApplicationContext.getConfigurableEnvironment();
        if(ObjectUtils.isEmpty(configurableEnvironment)){
            return null;
        }
        for (Map.Entry<String, Map<String, Object>> entry : configurableEnvironment.entrySet()) {
            Map<String, Object> value = entry.getValue();
            Object o = value.get(name);
            if(o != null){
                return o;
            }
        }
        return null;
    }

    @Override
    public String getString(String name) {
        return getMapValueGetter(name).getString(name);
    }

    @Override
    public Integer getInteger(String name) {
        return getMapValueGetter(name).getInteger(name);
    }

    @Override
    public Long getLong(String name) {
        return getMapValueGetter(name).getLong(name);
    }

    @Override
    public Double getDouble(String name) {
        return getMapValueGetter(name).getDouble(name);
    }

    @Override
    public Float getFloat(String name) {
        return getMapValueGetter(name).getFloat(name);
    }

    @Override
    public Boolean getBoolean(String name) {
        return getMapValueGetter(name).getBoolean(name);
    }

    @Override
    public Map<String, Map<String, Object>> getAll() {
        return mainApplicationContext.getConfigurableEnvironment();
    }

    private MapValueGetter getMapValueGetter(String name) {
        Map<String, Map<String, Object>> configurableEnvironment = mainApplicationContext.getConfigurableEnvironment();
        if(ObjectUtils.isEmpty(configurableEnvironment)){
            return new MapValueGetter(Collections.emptyMap());
        }
        for (Map.Entry<String, Map<String, Object>> entry : configurableEnvironment.entrySet()) {
            Map<String, Object> value = entry.getValue();
            if(value.containsKey(name)){
                return new MapValueGetter(value);
            }
        }
        return new MapValueGetter(Collections.emptyMap());
    }


}
