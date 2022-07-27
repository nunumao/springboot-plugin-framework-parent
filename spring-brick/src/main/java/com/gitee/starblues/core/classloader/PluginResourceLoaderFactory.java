package com.gitee.starblues.core.classloader;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderFactory;

/**
 * 插件资源工程
 *
 * @author starBlues
 * @version 3.0.4
 * @since 3.0.4
 */
public interface PluginResourceLoaderFactory extends ResourceLoaderFactory {

    /**
     * 加载插件资源
     * @param descriptor 插件资源描述
     * @throws Exception 添加插件资源异常
     * @since 3.0.4
     */
    void addResource(InsidePluginDescriptor descriptor) throws Exception;


}
