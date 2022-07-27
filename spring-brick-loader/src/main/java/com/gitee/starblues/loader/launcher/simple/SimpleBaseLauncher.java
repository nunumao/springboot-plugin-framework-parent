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

package com.gitee.starblues.loader.launcher.simple;

import com.gitee.starblues.loader.classloader.GeneralUrlClassLoader;
import com.gitee.starblues.loader.launcher.AbstractMainLauncher;
import com.gitee.starblues.loader.launcher.isolation.IsolationBaseLauncher;
import com.gitee.starblues.loader.launcher.runner.MethodRunner;


/**
 * simple 模式 launcher
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
public class SimpleBaseLauncher extends AbstractMainLauncher {

    private final MethodRunner methodRunner;

    public SimpleBaseLauncher(MethodRunner methodRunner) {
        this.methodRunner = methodRunner;
    }

    @Override
    protected ClassLoader createClassLoader(String... args) throws Exception {
        return new GeneralUrlClassLoader(IsolationBaseLauncher.MAIN_CLASS_LOADER_NAME, this.getClass().getClassLoader());
    }

    @Override
    protected ClassLoader launch(ClassLoader classLoader, String... args) throws Exception {
        methodRunner.run(classLoader);
        return classLoader;
    }
}
