package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.classloader.NestedPluginJarResourceLoader;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginLibInfo;
import com.gitee.starblues.core.descriptor.PluginType;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.launcher.plugin.involved.PluginLaunchInvolved;
import com.gitee.starblues.loader.classloader.GeneralUrlClassLoader;
import com.gitee.starblues.loader.launcher.AbstractLauncher;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.utils.FilesUtils;
import com.gitee.starblues.utils.MsgUtils;
import com.gitee.starblues.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Set;

/**
 * 插件共享式启动引导
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
@Slf4j
public class PluginCoexistLauncher extends AbstractLauncher<SpringPluginHook> {

    protected final PluginInteractive pluginInteractive;
    protected final PluginLaunchInvolved pluginLaunchInvolved;

    public PluginCoexistLauncher(PluginInteractive pluginInteractive,
                                 PluginLaunchInvolved pluginLaunchInvolved) {
        this.pluginInteractive = pluginInteractive;
        this.pluginLaunchInvolved = pluginLaunchInvolved;
    }

    @Override
    protected ClassLoader createClassLoader(String... args) throws Exception {
        GeneralUrlClassLoader classLoader = new GeneralUrlClassLoader(
                pluginInteractive.getPluginDescriptor().getPluginId(),
                this.getClass().getClassLoader());
        addClasspath(classLoader);
        return classLoader;
    }

    @Override
    protected SpringPluginHook launch(ClassLoader classLoader, String... args) throws Exception {
        InsidePluginDescriptor pluginDescriptor = pluginInteractive.getPluginDescriptor();
        pluginLaunchInvolved.before(pluginDescriptor, classLoader);
        try {
            SpringPluginHook springPluginHook = (SpringPluginHook) new PluginMethodRunner(pluginInteractive)
                    .run(classLoader);
            if(springPluginHook == null){
                throw new PluginException("插件返回的 SpringPluginHook 不能为空");
            }
            pluginLaunchInvolved.after(pluginDescriptor, classLoader, springPluginHook);
            return new SpringPluginHookWrapper(springPluginHook, pluginDescriptor, pluginLaunchInvolved, classLoader);
        } catch (Throwable throwable){
            pluginLaunchInvolved.failure(pluginDescriptor,classLoader, throwable);
            throw throwable;
        }
    }

    private void addClasspath(GeneralUrlClassLoader classLoader) throws Exception {
        InsidePluginDescriptor pluginDescriptor = pluginInteractive.getPluginDescriptor();
        PluginType pluginType = pluginDescriptor.getType();
        if(PluginType.isNestedPackage(pluginType)){
            classLoader.addResource(new NestedPluginJarResourceLoader(
                    pluginDescriptor, null, classLoader
            ));

        } else if(PluginType.isOuterPackage(pluginType)){
            // TODO addOuterPluginClasspath(descriptor);
            addLibFile(classLoader);
        } else {
            // TODO addDirPluginClasspath(descriptor);
            addLibFile(classLoader);
        }
        String pluginClassPath = pluginDescriptor.getPluginClassPath();
        classLoader.addResource(pluginClassPath);
    }

    private void addLibFile(GeneralUrlClassLoader classLoader) throws Exception {
        InsidePluginDescriptor pluginDescriptor = pluginInteractive.getPluginDescriptor();
        Set<PluginLibInfo> pluginLibInfos = pluginDescriptor.getPluginLibInfo();
        String pluginUnique = MsgUtils.getPluginUnique(pluginDescriptor);
        if(ObjectUtils.isEmpty(pluginLibInfos)){
            log.warn("插件[{}]依赖为空！", pluginUnique);
            return;
        }
        for (PluginLibInfo pluginLibInfo : pluginLibInfos) {
            File existFile = FilesUtils.getExistFile(pluginLibInfo.getPath());
            if(existFile != null){
                classLoader.addResource(existFile);
                log.debug("插件[{}]依赖被加载: {}", pluginUnique, existFile.getPath());
            }
        }
    }

}
