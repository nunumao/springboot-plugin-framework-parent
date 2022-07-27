package com.gitee.starblues.loader.classloader;

import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.loader.DefaultResource;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoader;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * classloader 转换器
 *
 * @author starBlues
 * @version 3.0.4
 * @since 3.0.4
 */
public class ClassLoaderTranslator implements ResourceLoaderFactory {

    private final URLClassLoader classLoader;

    public ClassLoaderTranslator(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void addResource(String path) throws Exception {
        throw new RuntimeException("Does not support!");
    }

    @Override
    public void addResource(File file) throws Exception {
        throw new RuntimeException("Does not support!");
    }

    @Override
    public void addResource(Path path) throws Exception {
        throw new RuntimeException("Does not support!");
    }

    @Override
    public void addResource(URL url) throws Exception {
        throw new RuntimeException("Does not support!");
    }

    @Override
    public void addResource(ResourceLoader resourceLoader) throws Exception {
        throw new RuntimeException("Does not support!");
    }

    @Override
    public Resource findFirstResource(String name) {
        URL url = classLoader.getResource(name);
        if(url == null){
            return null;
        }
        return new DefaultResource(name, url, url);
    }

    @Override
    public Enumeration<Resource> findAllResource(String name) {
        try {
            Enumeration<URL> resources = classLoader.getResources(name);
            return new Enumeration<Resource>() {
                @Override
                public boolean hasMoreElements() {
                    return resources.hasMoreElements();
                }

                @Override
                public Resource nextElement() {
                    URL url = resources.nextElement();
                    if(url == null){
                        return null;
                    }
                    return new DefaultResource(name, url, url);
                }
            };
        } catch (IOException e) {
            return Collections.emptyEnumeration();
        }
    }

    @Override
    public InputStream getInputStream(String name) {
        return classLoader.getResourceAsStream(name);
    }

    @Override
    public List<URL> getUrls() {
        return Arrays.asList(classLoader.getURLs());
    }

    @Override
    public void close() throws Exception {

    }
}
