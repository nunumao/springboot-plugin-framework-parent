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

import com.gitee.starblues.loader.launcher.DevelopmentModeSetting;
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


    private static final ThreadLocal<Boolean> LAUNCH_PLUGIN = new ThreadLocal<Boolean>();

    public static void setLaunchPlugin() {
        LAUNCH_PLUGIN.set(true);
    }

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        if(DevelopmentModeSetting.isolation()){
            return new IsolationDisableAutoConfiguration().match(autoConfigurationClasses, autoConfigurationMetadata);
        } else if(DevelopmentModeSetting.coexist()){
            return new CoexistDisableAutoConfiguration().match(autoConfigurationClasses, autoConfigurationMetadata);
        } else {
            boolean[] permitAll = new boolean[autoConfigurationClasses.length];
            for (int i = 0; i < autoConfigurationClasses.length; i++) {
                permitAll[i] = true;
            }
            return permitAll;
        }
    }


    private static class IsolationDisableAutoConfiguration implements AutoConfigurationImportFilter{

        private final List<String> disableFuzzyClass = new ArrayList<>();

        IsolationDisableAutoConfiguration(){
            addDisableFuzzyClasses();
        }

        private void addDisableFuzzyClasses() {
            disableFuzzyClass.add("org.springframework.boot.autoconfigure.http");
            disableFuzzyClass.add("org.springframework.boot.autoconfigure.web");
            disableFuzzyClass.add("org.springframework.boot.autoconfigure.websocket");
            disableFuzzyClass.add("org.springframework.boot.autoconfigure.jackson");
            disableFuzzyClass.add("org.springframework.boot.autoconfigure.webservices");
        }

        private boolean isDisabled(String className){
            if(ObjectUtils.isEmpty(className)){
                return false;
            }
            for (String disableFuzzyClass : disableFuzzyClass) {
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


    private static class CoexistDisableAutoConfiguration implements AutoConfigurationImportFilter{

        @Override
        public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
            Boolean launchPlugin = LAUNCH_PLUGIN.get();
            boolean[] match = new boolean[autoConfigurationClasses.length];
            try {
                if(launchPlugin != null && launchPlugin){
                    return match;
                } else {
                    for (int i = 0; i < autoConfigurationClasses.length; i++) {
                        match[i] = true;
                    }
                }
                return match;
            } finally {
                LAUNCH_PLUGIN.remove();
            }
        }
    }

}
