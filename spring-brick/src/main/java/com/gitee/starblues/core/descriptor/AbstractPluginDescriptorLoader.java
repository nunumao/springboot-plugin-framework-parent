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

package com.gitee.starblues.core.descriptor;


import com.gitee.starblues.common.AbstractDependencyPlugin;
import com.gitee.starblues.common.Constants;
import com.gitee.starblues.common.DependencyPlugin;
import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.core.descriptor.decrypt.PluginDescriptorDecrypt;
import com.gitee.starblues.core.exception.PluginDecryptException;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.utils.FilesUtils;
import com.gitee.starblues.utils.MsgUtils;
import com.gitee.starblues.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.gitee.starblues.common.PluginDescriptorKey.*;
import static com.gitee.starblues.utils.PropertiesUtils.getValue;

/**
 * 抽象的 PluginDescriptorLoader
 * @author starBlues
 * @version 3.0.2
 */
@Slf4j
public abstract class AbstractPluginDescriptorLoader implements PluginDescriptorLoader{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final PluginDescriptorDecrypt pluginDescriptorDecrypt;

    protected AbstractPluginDescriptorLoader(PluginDescriptorDecrypt pluginDescriptorDecrypt) {
        this.pluginDescriptorDecrypt = pluginDescriptorDecrypt;
    }

    @Override
    public InsidePluginDescriptor load(Path location) throws PluginException {
        PluginMeta pluginMeta = null;
        try {
            pluginMeta = getPluginMetaInfo(location);
            if(pluginMeta == null || pluginMeta.getProperties() == null){
                logger.debug("路径[{}]没有发现插件配置信息", location);
                return null;
            }
            return create(pluginMeta, location);
        } catch (Throwable e) {
            if(e instanceof PluginException){
                throw (PluginException) e;
            } else {
                throw new PluginException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void close() throws Exception {

    }

    /**
     * 子类获取插件信息
     * @param location 路径
     * @return Properties
     * @throws Exception 异常
     */
    protected abstract PluginMeta getPluginMetaInfo(Path location) throws Exception;

    protected DefaultInsidePluginDescriptor create(PluginMeta pluginMeta, Path path) throws Exception{
        Properties properties = pluginMeta.getProperties();
        DefaultInsidePluginDescriptor descriptor = new DefaultInsidePluginDescriptor(
                getValue(properties, PLUGIN_ID),
                getValue(properties, PLUGIN_VERSION),
                getValue(properties, PLUGIN_BOOTSTRAP_CLASS),
                path
        );
        descriptor.setType(PluginType.byName(pluginMeta.getPackageType()));

        PluginResourcesConfig resourcesConfig = getPluginResourcesConfig(path, properties);

        String pluginLibDir = getValue(properties, PLUGIN_LIB_DIR, false);
        descriptor.setPluginLibDir(pluginLibDir);
        descriptor.setPluginLibInfo(getPluginLibInfo(descriptor, resourcesConfig.getDependenciesIndex()));
        descriptor.setIncludeMainResourcePatterns(resourcesConfig.getLoadMainResourceIncludes());
        descriptor.setExcludeMainResourcePatterns(resourcesConfig.getLoadMainResourceExcludes());

        descriptor.setProperties(properties);
        descriptor.setPluginClassPath(getValue(properties, PLUGIN_PATH, false));
        descriptor.setDescription(getValue(properties, PLUGIN_DESCRIPTION, false));
        descriptor.setRequires(getValue(properties, PLUGIN_REQUIRES, false));
        descriptor.setProvider(getValue(properties, PLUGIN_PROVIDER, false));
        descriptor.setLicense(getValue(properties, PLUGIN_LICENSE, false));
        descriptor.setConfigFileName(getValue(properties, PLUGIN_CONFIG_FILE_NAME, false));
        descriptor.setConfigFileLocation(getValue(properties, PLUGIN_CONFIG_FILE_LOCATION, false));
        descriptor.setArgs(getValue(properties, PLUGIN_ARGS, false));

        descriptor.setDependencyPlugins(getPluginDependency(properties));
        return descriptor;
    }


    protected List<DependencyPlugin> getPluginDependency(Properties properties){
        return AbstractDependencyPlugin.toList(getValue(properties, PLUGIN_DEPENDENCIES, false),
                DefaultDependencyPlugin::new);
    }

    protected PluginResourcesConfig getPluginResourcesConfig(Path path, Properties properties) throws Exception{
        String libIndex = getValue(properties, PLUGIN_RESOURCES_CONFIG);
        if(ObjectUtils.isEmpty(libIndex)){
            return new PluginResourcesConfig();
        }
        File file = new File(libIndex);
        if(!file.exists()){
            // 如果绝对路径不存在, 则判断相对路径
            String pluginPath = getValue(properties, PLUGIN_PATH);
            file = new File(FilesUtils.joiningFilePath(pluginPath, libIndex));
        }
        if(!file.exists()){
            // 都不存在, 则返回为空
            return new PluginResourcesConfig();
        }
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            return PluginResourcesConfig.parse(lines);
        } catch (IOException e) {
            throw new Exception("Load plugin lib index path failure. " + libIndex, e);
        }
    }

    protected Set<PluginLibInfo> getPluginLibInfo(DefaultInsidePluginDescriptor descriptor, Set<String> dependenciesIndex){
        String pluginLibDir = descriptor.getPluginLibDir();
        boolean configPluginLibDir = false;
        if(!ObjectUtils.isEmpty(pluginLibDir)){
            descriptor.setPluginLibDir(getLibDir(descriptor, pluginLibDir));
            configPluginLibDir = true;
        }
        if(ObjectUtils.isEmpty(dependenciesIndex)){
            return Collections.emptySet();
        }
        Set<PluginLibInfo> pluginLibInfos = new HashSet<>(dependenciesIndex.size());
        for (String index : dependenciesIndex) {
            boolean loadToMain;
            if(index.endsWith(Constants.LOAD_TO_MAIN_SIGN)){
                index = index.substring(0, index.lastIndexOf(Constants.LOAD_TO_MAIN_SIGN));
                loadToMain = true;
            } else {
                loadToMain = false;
            }
            String libPath = index;
            if(configPluginLibDir){
                libPath = getLibPath(descriptor, index);
            }
            pluginLibInfos.add(new PluginLibInfo(libPath, loadToMain));
        }
        return pluginLibInfos;
    }

    protected String getLibDir(DefaultInsidePluginDescriptor descriptor, String configPluginLibDir){
        if(!FilesUtils.isRelativePath(configPluginLibDir)){
            return configPluginLibDir;
        }
        // 是相对路径
        // 先相对当前插件目录
        String resolveRelativePath = FilesUtils.resolveRelativePath(descriptor.getPluginPath(), configPluginLibDir);
        if(new File(resolveRelativePath).exists()){
            return resolveRelativePath;
        }
        // 再相对插件存放目录
        resolveRelativePath = FilesUtils.resolveRelativePath(new File(descriptor.getPluginPath()).getParent(), configPluginLibDir);
        if(new File(resolveRelativePath).exists()){
            return resolveRelativePath;
        }
        // 最后相对主程序目录
        resolveRelativePath = FilesUtils.resolveRelativePath(new File("").getAbsolutePath(), configPluginLibDir);
        if(new File(resolveRelativePath).exists()){
            return resolveRelativePath;
        }
        throw new PluginException("插件["+ MsgUtils.getPluginUnique(descriptor) +"]" +
                "依赖目录[" + descriptor.getPluginLibDir() + "]不存在!");
    }

    protected String getLibPath(DefaultInsidePluginDescriptor descriptor, String index){
        return FilesUtils.joiningFilePath(descriptor.getPluginLibDir(), index);
    }

    protected Properties getDecryptProperties(InputStream inputStream) throws Exception{
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);){
            properties.load(reader);
        }
        String pluginId = getValue(properties, PLUGIN_ID);
        return pluginDescriptorDecrypt.decrypt(pluginId, properties);
    }

    @AllArgsConstructor
    @Getter
    public static class PluginMeta{
        private final String packageType;
        private final Properties properties;
    }

}
