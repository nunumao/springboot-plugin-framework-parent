package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.PluginInsideInfo;
import com.gitee.starblues.core.classloader.NestedPluginJarResourceLoader;
import com.gitee.starblues.core.classloader.PluginGeneralUrlClassLoader;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginLibInfo;
import com.gitee.starblues.core.descriptor.PluginType;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.launcher.plugin.involved.PluginLaunchInvolved;
import com.gitee.starblues.loader.classloader.GeneralUrlClassLoader;
import com.gitee.starblues.loader.launcher.AbstractLauncher;
import com.gitee.starblues.loader.launcher.LauncherContext;
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
 * @version 3.1.0
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
        PluginGeneralUrlClassLoader classLoader = new PluginGeneralUrlClassLoader(
                pluginInteractive.getPluginDescriptor().getPluginId(),
                getParentClassLoader());
        classLoader.addResource(pluginInteractive.getPluginDescriptor());
        return classLoader;
    }

    @Override
    protected SpringPluginHook launch(ClassLoader classLoader, String... args) throws Exception {
        InsidePluginDescriptor pluginDescriptor = pluginInteractive.getPluginDescriptor();
        PluginInsideInfo pluginInsideInfo = pluginInteractive.getPluginInsideInfo();
        pluginLaunchInvolved.before(pluginInsideInfo, classLoader);
        try {
            SpringPluginHook springPluginHook = (SpringPluginHook) new PluginMethodRunner(pluginInteractive)
                    .run(classLoader);
            if(springPluginHook == null){
                throw new PluginException("插件返回的 SpringPluginHook 不能为空");
            }
            pluginLaunchInvolved.after(pluginInsideInfo, classLoader, springPluginHook);
            return new SpringPluginHookWrapper(springPluginHook, pluginInsideInfo, pluginLaunchInvolved, classLoader);
        } catch (Throwable throwable){
            pluginLaunchInvolved.failure(pluginInsideInfo,classLoader, throwable);
            throw throwable;
        }
    }

    protected GeneralUrlClassLoader getParentClassLoader() throws Exception {
        ClassLoader contextClassLoader = LauncherContext.getMainClassLoader();
        if(contextClassLoader instanceof GeneralUrlClassLoader){
            return (GeneralUrlClassLoader) contextClassLoader;
        } else {
            throw new Exception("非法父类加载器: " + contextClassLoader.getClass().getName());
        }
    }

}
