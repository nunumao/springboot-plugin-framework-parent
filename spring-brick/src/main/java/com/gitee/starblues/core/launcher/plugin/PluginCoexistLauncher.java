package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.common.Constants;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginLibInfo;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.launcher.plugin.involved.PluginLaunchInvolved;
import com.gitee.starblues.loader.DevelopmentMode;
import com.gitee.starblues.loader.classloader.GeneralUrlClassLoader;
import com.gitee.starblues.loader.launcher.AbstractLauncher;
import com.gitee.starblues.spring.SpringPluginHook;

import java.util.Set;

/**
 * 插件共享式启动引导
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
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
        GeneralUrlClassLoader classLoader = new GeneralUrlClassLoader(this.getClass().getClassLoader());
        InsidePluginDescriptor pluginDescriptor = pluginInteractive.getPluginDescriptor();
        String pluginClassPath = pluginDescriptor.getPluginClassPath();
        classLoader.addUrl(pluginClassPath);
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

}
