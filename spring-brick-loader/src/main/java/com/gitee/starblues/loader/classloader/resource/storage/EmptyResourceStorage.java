package com.gitee.starblues.loader.classloader.resource.storage;

import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.ResourceByteGetter;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * 空的资源存储
 *
 * @author starBlues
 * @version 3.0.4
 * @since 3.0.4
 */
public class EmptyResourceStorage implements ResourceStorage{
    @Override
    public void add(String name, URL url, ResourceByteGetter byteGetter) throws Exception {

    }

    @Override
    public void add(String name, URL url) throws Exception {

    }

    @Override
    public boolean exist(String name) {
        return false;
    }

    @Override
    public Resource get(String name) {
        return null;
    }

    @Override
    public InputStream getInputStream(String name) {
        return null;
    }

    @Override
    public List<Resource> getAll() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void close() throws Exception {

    }
}
