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

package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.PluginInsideInfo;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.exception.PluginProhibitStopException;
import com.gitee.starblues.core.launcher.plugin.involved.PluginLaunchInvolved;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.spring.WebConfig;
import com.gitee.starblues.spring.web.thymeleaf.ThymeleafConfig;
import com.gitee.starblues.utils.ResourceUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * SpringPluginHook-Wrapper
 * @author starBlues
 * @version 3.0.0
 */
@Slf4j
public class SpringPluginHookWrapper implements SpringPluginHook {

    private final SpringPluginHook target;
    private final PluginInsideInfo pluginInsideInfo;
    private final PluginLaunchInvolved pluginLaunchInvolved;
    private final ClassLoader classLoader;

    public SpringPluginHookWrapper(SpringPluginHook target, PluginInsideInfo pluginInsideInfo,
                                   PluginLaunchInvolved pluginLaunchInvolved,
                                   ClassLoader classLoader) {
        this.target = target;
        this.pluginInsideInfo = pluginInsideInfo;
        this.pluginLaunchInvolved = pluginLaunchInvolved;
        this.classLoader = classLoader;
    }

    @Override
    public void stopVerify() throws PluginProhibitStopException {
        target.stopVerify();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return target.getApplicationContext();
    }

    @Override
    public WebConfig getWebConfig() {
        return target.getWebConfig();
    }

    @Override
    public ThymeleafConfig getThymeleafConfig() {
        return null;
    }

    @Override
    public void close(boolean isUninstall) throws Exception {
        try {
            pluginLaunchInvolved.close(pluginInsideInfo, classLoader);
        } catch (Exception e){
            log.error("关闭插件异常: {}", e.getMessage(), e);
        }
        try {
            target.close(isUninstall);
        } finally {
            ResourceUtils.closeQuietly(classLoader);
        }
    }
}
