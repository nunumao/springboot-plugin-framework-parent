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

package com.gitee.starblues.core.checker;

import com.gitee.starblues.core.DefaultPluginInsideInfo;
import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.RealizeProvider;
import com.gitee.starblues.core.descriptor.DefaultInsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.version.SemverVersionInspector;
import com.gitee.starblues.integration.IntegrationConfiguration;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.nio.file.Path;

import static org.powermock.api.mockito.PowerMockito.*;
/**
 * 测试 DefaultPluginLauncherChecker
 *
 * @author starBlues
 * @version 3.0.0
 */
@RunWith(PowerMockRunner.class)
public class DefaultPluginIsolationLauncherCheckerTest extends TestCase {

    private DefaultPluginLauncherChecker launcherChecker;

    @Mock
    private RealizeProvider realizeProvider;
    @Mock
    private IntegrationConfiguration configuration;

    @Before
    public void setUp(){
        launcherChecker = spy(new DefaultPluginLauncherChecker(realizeProvider, configuration));
    }

    @Test
    public void test_checkCanStart_start(){
        DefaultPluginInsideInfo pluginInfo = getPluginInfo();
        pluginInfo.setPluginState(PluginState.STARTED);

        when(configuration.isDisabled(Mockito.anyString())).thenReturn(false);
        when(configuration.isEnable(Mockito.anyString())).thenReturn(true);

        try {
            launcherChecker.checkCanStart(pluginInfo);
        } catch (Exception e){
            assertTrue(e instanceof PluginException);
        }
    }


    private DefaultPluginInsideInfo getPluginInfo(){
        String pluginId = "pluginId";
        String version = "1.0.0";
        String pluginClass = "pluginClass";
        Path pluginPath = mock(Path.class);
        Path pluginPath2 = mock(Path.class);
        when(pluginPath.toAbsolutePath()).thenReturn(pluginPath2);
        when(pluginPath2.toString()).thenReturn("path");

        File file = mock(File.class);
        when(pluginPath.toFile()).thenReturn(file);
        when(file.getName()).thenReturn("file.jar");
        InsidePluginDescriptor descriptor = new DefaultInsidePluginDescriptor(pluginId, version, pluginClass, pluginPath);

        return new DefaultPluginInsideInfo(descriptor);
    }

}