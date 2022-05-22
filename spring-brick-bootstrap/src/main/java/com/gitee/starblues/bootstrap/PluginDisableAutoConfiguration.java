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

import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件禁用的 AutoConfiguration
 *
 * @author starBlues
 * @version 3.0.3
 */
public class PluginDisableAutoConfiguration implements AutoConfigurationImportFilter {

    private static final List<String> DISABLE_FUZZY_CLASSES = new ArrayList<>();

    public PluginDisableAutoConfiguration(){
        addDisableFuzzyClasses();
    }

    private void addDisableFuzzyClasses() {
        DISABLE_FUZZY_CLASSES.add("org.springframework.boot.autoconfigure.http");
        DISABLE_FUZZY_CLASSES.add("org.springframework.boot.autoconfigure.web");
        DISABLE_FUZZY_CLASSES.add("org.springframework.boot.autoconfigure.websocket");
        DISABLE_FUZZY_CLASSES.add("org.springframework.boot.autoconfigure.jackson");
        DISABLE_FUZZY_CLASSES.add("org.springframework.boot.autoconfigure.webservices");
    }

    public static boolean isDisabled(String className){
        if(ObjectUtils.isEmpty(className)){
            return false;
        }
        for (String disableFuzzyClass : DISABLE_FUZZY_CLASSES) {
            if (className.contains(disableFuzzyClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] match = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            String autoConfigurationClass = autoConfigurationClasses[i];
            if(autoConfigurationClass == null || "".equals(autoConfigurationClass)){
                continue;
            }
            match[i] = !isDisabled(autoConfigurationClass);
        }
        return match;
    }
}
