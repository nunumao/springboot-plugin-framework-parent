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
import com.gitee.starblues.loader.launcher.runner.MethodRunner;
import com.gitee.starblues.loader.utils.ObjectUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.loader.LoaderConstant.*;
/**
 * 主程序生成环境启动引导器
 * @author starBlues
 * @version 3.0.0
 */
public class SpringMainProdBootstrap {


    public static void main(String[] args) throws Exception {
        JarFile.registerUrlProtocolHandler();
        new SpringMainProdBootstrap().run(args);
    }

    private void run(String[] args) throws Exception{
        File rootJarFile = getRootJarFile();
        String startClass = null;
        String mainPackageType;
        try (JarFile jarFile = new JarFile(rootJarFile)){
            Manifest manifest = jarFile.getManifest();
            IllegalStateException exception = new IllegalStateException("当前启动包非法包!");
            if(manifest == null || manifest.getMainAttributes() == null){
                throw exception;
            }
            Attributes mainAttributes = manifest.getMainAttributes();
            startClass = mainAttributes.getValue(START_CLASS);
            if (ObjectUtils.isEmpty(startClass)) {
                throw exception;
            }
            mainPackageType = mainAttributes.getValue(MAIN_PACKAGE_TYPE);
        }
        MethodRunner methodRunner = new MethodRunner(startClass, SpringMainBootstrap.SPRING_BOOTSTRAP_RUN_METHOD, args);
        Launcher<ClassLoader> launcher;
        if(Objects.equals(mainPackageType, MAIN_PACKAGE_TYPE_JAR_OUTER)){
            launcher = new MainJarOuterProgramLauncher(methodRunner, rootJarFile);
        } else {
            launcher = new MainJarProgramLauncher(methodRunner, rootJarFile);
        }
        launcher.run(args);
    }

    private File getRootJarFile() throws URISyntaxException {
        ProtectionDomain protectionDomain = SpringMainBootstrap.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = (codeSource != null) ? codeSource.getLocation().toURI() : null;
        String path = (location != null) ? location.getSchemeSpecificPart() : null;
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }
        File root = new File(path);
        if (!root.exists()) {
            throw new IllegalStateException("Unable to determine code source archive from " + root);
        }
        return root;
    }

}
