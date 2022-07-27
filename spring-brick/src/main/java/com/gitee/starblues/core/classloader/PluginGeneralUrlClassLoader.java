package com.gitee.starblues.core.classloader;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginLibInfo;
import com.gitee.starblues.core.descriptor.PluginType;
import com.gitee.starblues.core.exception.PluginException;
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
public class PluginGeneralUrlClassLoader extends GeneralUrlClassLoader implements PluginResourceLoaderFactory{

    private final PluginResourceLoaderFactory proxy;

    public PluginGeneralUrlClassLoader(String name, GeneralUrlClassLoader parent) {
        super(name, parent);
        this.proxy = new PluginResourceLoaderFactoryProxy(this, parent);
    }

    @Override
    public void addResource(InsidePluginDescriptor descriptor) throws Exception {
        proxy.addResource(descriptor);
    }

}
