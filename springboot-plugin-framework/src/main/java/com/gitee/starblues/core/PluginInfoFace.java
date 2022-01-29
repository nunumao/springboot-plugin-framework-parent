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

package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.Assert;

/**
 * 外部 PluginWrapperFace
 * @author starBlues
 * @version 3.0.0
 */
public class PluginInfoFace implements PluginInfo {

    private final PluginDescriptor pluginDescriptor;
    private final PluginState pluginState;
    private final boolean followSystem;

    public PluginInfoFace(PluginInsideInfo pluginInsideInfo) {
        Assert.isNotNull(pluginInsideInfo, "参数 pluginWrapperInside 不能为空");
        this.pluginDescriptor = pluginInsideInfo.getPluginDescriptor().toPluginDescriptor();
        this.pluginState = pluginInsideInfo.getPluginState();
        this.followSystem = pluginInsideInfo.isFollowSystem();
    }

    @Override
    public String getPluginId() {
        return pluginDescriptor.getPluginId();
    }

    @Override
    public PluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }

    @Override
    public String getPluginPath() {
        return pluginDescriptor.getPluginPath();
    }

    @Override
    public PluginState getPluginState() {
        return pluginState;
    }

    @Override
    public boolean isFollowSystem() {
        return followSystem;
    }
}
