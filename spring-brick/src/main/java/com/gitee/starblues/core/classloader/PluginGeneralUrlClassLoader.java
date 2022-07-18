package com.gitee.starblues.core.classloader;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginLibInfo;
import com.gitee.starblues.core.descriptor.PluginType;
import com.gitee.starblues.loader.classloader.GeneralUrlClassLoader;
import com.gitee.starblues.loader.launcher.LauncherContext;
import com.gitee.starblues.utils.FilesUtils;
import com.gitee.starblues.utils.MsgUtils;
import com.gitee.starblues.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Set;

/**
 * 插件基本 url classLoader
 *
 * @author starBlues
 * @version 3.0.4
 * @since 3.0.4
 */
@Slf4j
public class PluginGeneralUrlClassLoader extends GeneralUrlClassLoader {

    public PluginGeneralUrlClassLoader(String name, ClassLoader parent) {
        super(name, parent);
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


    private void addLibFile(InsidePluginDescriptor pluginDescriptor) throws Exception {
        Set<PluginLibInfo> pluginLibInfos = pluginDescriptor.getPluginLibInfo();
        if(ObjectUtils.isEmpty(pluginLibInfos)){
            return;
        }
        String pluginUnique = MsgUtils.getPluginUnique(pluginDescriptor);
        String pluginLibDir = pluginDescriptor.getPluginLibDir();
        if(!ObjectUtils.isEmpty(pluginLibDir)){
            log.info("插件[{}]依赖加载目录: {}", pluginUnique, pluginLibDir);
        }
        if(pluginLibInfos.isEmpty()){
            log.warn("插件[{}]依赖为空！", pluginUnique);
            return;
        }

        GeneralUrlClassLoader parentClassLoader = (GeneralUrlClassLoader) LauncherContext.getMainClassLoader();


        for (PluginLibInfo pluginLibInfo : pluginLibInfos) {
            File existFile = FilesUtils.getExistFile(pluginLibInfo.getPath());
            if(existFile != null){
                if(pluginLibInfo.isLoadToMain()){
                    // 加载到主程序中
                    parentClassLoader.addFile(existFile);
                    log.debug("插件[{}]依赖被加载到主程序中: {}", pluginUnique, existFile.getPath());
                } else {
                    super.addFile(existFile);
                    log.debug("插件[{}]依赖被加载: {}", pluginUnique, existFile.getPath());
                }
            }
        }
    }

}
