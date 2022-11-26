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

package com.gitee.starblues.loader.launcher;

import com.gitee.starblues.loader.jar.JarFile;

/**
 * 主程序生成环境启动引导器
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.0
 */
public class SpringMainProdBootstrap {

    public static void main(String[] args) throws Exception {
        JarFile.registerUrlProtocolHandler();
        new SpringMainProdBootstrap().run(args);
    }

    private void run(String[] args) throws Exception{
        Launcher<ClassLoader> launcher = new ProdLauncher();
        launcher.run(args);
    }

}
