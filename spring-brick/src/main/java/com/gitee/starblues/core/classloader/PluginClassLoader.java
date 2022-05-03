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

package com.gitee.starblues.core.classloader;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginLibInfo;
import com.gitee.starblues.core.descriptor.PluginType;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.loader.classloader.*;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderFactory;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.FilesUtils;
import com.gitee.starblues.utils.MsgUtils;
import com.gitee.starblues.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

/**
 * 插件 classLoader
 * @author starBlues
 * @version 3.0.0
 */
@Slf4j
public class PluginClassLoader extends GenericClassLoader {

    private final GenericClassLoader parentClassLoader;
    private MainResourceMatcher mainResourceMatcher;

    public PluginClassLoader(String name, GenericClassLoader parentClassLoader, MainResourcePatternDefiner patternDefiner,
                             ResourceLoaderFactory resourceLoaderFactory) {
        super(name, parentClassLoader, resourceLoaderFactory);
        this.parentClassLoader = parentClassLoader;
        if(patternDefiner != null){
            setMainResourceMatcher(new CacheMainResourceMatcher(patternDefiner));
        } else {
            setMainResourceMatcher(new ProhibitMainResourceMatcher());
        }
    }

    public void setMainResourceMatcher(MainResourceMatcher mainResourceMatcher){
        this.mainResourceMatcher = Assert.isNotNull(mainResourceMatcher, "参数 mainResourceMatcher 不能为空");
    }

    public void addResource(InsidePluginDescriptor descriptor) throws Exception {
        PluginType pluginType = descriptor.getType();
        if(PluginType.isNestedPackage(pluginType)){
            NestedPluginJarResourceLoader resourceLoader =
                    new NestedPluginJarResourceLoader(descriptor, parentClassLoader, resourceLoaderFactory);
            resourceLoaderFactory.addResource(resourceLoader);
        } else if(PluginType.isOuterPackage(pluginType)){
            addOuterPluginClasspath(descriptor);
            addLibFile(descriptor);
        } else {
            addDirPluginClasspath(descriptor);
            addLibFile(descriptor);
        }
    }

    private void addOuterPluginClasspath(InsidePluginDescriptor descriptor) throws Exception{
        String pluginPath = descriptor.getPluginPath();
        File existFile = FilesUtils.getExistFile(pluginPath);
        if(existFile != null){
            addResource(existFile);
            log.debug("插件[{}]Classpath已被加载: {}", MsgUtils.getPluginUnique(descriptor), existFile.getPath());
        } else {
            throw new PluginException("没有发现插件路径: " + pluginPath);
        }
    }

    private void addDirPluginClasspath(InsidePluginDescriptor descriptor) throws Exception {
        String pluginClassPath = descriptor.getPluginClassPath();
        File existFile = FilesUtils.getExistFile(pluginClassPath);
        if(existFile != null){
            addResource(existFile);
            log.debug("插件[{}]Classpath已被加载: {}", MsgUtils.getPluginUnique(descriptor), existFile.getPath());
        }
    }

    private void addLibFile(InsidePluginDescriptor pluginDescriptor) throws Exception {
        Set<PluginLibInfo> pluginLibInfos = pluginDescriptor.getPluginLibInfo();
        if(ObjectUtils.isEmpty(pluginLibInfos)){
            return;
        }
        String pluginUnique = MsgUtils.getPluginUnique(pluginDescriptor);
        log.info("插件[{}]依赖加载目录: {}", pluginUnique, pluginDescriptor.getPluginLibDir());
        if(pluginLibInfos.isEmpty()){
            log.warn("插件[{}]依赖为空！", pluginUnique);
            return;
        }
        for (PluginLibInfo pluginLibInfo : pluginLibInfos) {
            File existFile = FilesUtils.getExistFile(pluginLibInfo.getPath());
            if(existFile != null){
                if(pluginLibInfo.isLoadToMain()){
                    // 加载到主程序中
                    parentClassLoader.addResource(existFile);
                    log.debug("插件[{}]依赖被加载到主程序中: {}", pluginUnique, existFile.getPath());
                } else {
                    addResource(existFile);
                    log.debug("插件[{}]依赖被加载: {}", pluginUnique, existFile.getPath());
                }
            }
        }
    }


    @Override
    protected Class<?> findClassFromParent(String className) throws ClassNotFoundException {
        if(mainResourceMatcher.match(className.replace(".", "/"))){
            try {
                return super.findClassFromParent(className);
            } catch (Exception e){
                // 忽略
            }
        }
        return null;
    }

    @Override
    protected InputStream findInputStreamFromParent(String name) {
        if(mainResourceMatcher.match(name)){
            try {
                return super.findInputStreamFromParent(name);
            } catch (Exception e){
                // 忽略
            }
        }
        return null;
    }

    @Override
    protected URL findResourceFromParent(String name) {
        if(mainResourceMatcher.match(name)){
            return super.findResourceFromParent(name);
        }
        return null;
    }

    @Override
    protected Enumeration<URL> findResourcesFromParent(String name) throws IOException {
        if(mainResourceMatcher.match(name)){
            return super.findResourcesFromParent(name);
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if(mainResourceMatcher instanceof AutoCloseable){
            try {
                ((AutoCloseable) mainResourceMatcher).close();
            } catch (Exception e){
                throw new IOException(e);
            }
        }
    }

}
