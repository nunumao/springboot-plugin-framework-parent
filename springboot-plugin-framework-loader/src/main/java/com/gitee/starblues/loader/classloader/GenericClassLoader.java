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

package com.gitee.starblues.loader.classloader;


import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoader;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderFactory;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderGetter;
import com.gitee.starblues.loader.utils.Assert;
import com.gitee.starblues.loader.utils.IOUtils;
import com.gitee.starblues.loader.utils.ObjectUtils;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基本的 ClassLoader
 * @author starBlues
 * @version 3.0.0
 */
public class GenericClassLoader extends URLClassLoader {

    private final String name;
    private final ClassLoader parent;

    protected final ResourceLoaderFactory resourceLoaderFactory;

    private final Map<String, Class<?>> pluginClassCache = new ConcurrentHashMap<>();

    public GenericClassLoader(String name, ResourceLoaderFactory resourceLoaderFactory) {
        this(name, null, resourceLoaderFactory);
    }

    public GenericClassLoader(String name, ClassLoader parent, ResourceLoaderFactory resourceLoaderFactory) {
        super(new URL[]{}, null);
        this.name = Assert.isNotEmpty(name, "name 不能为空");
        this.resourceLoaderFactory = Assert.isNotNull(resourceLoaderFactory, "resourceLoaderFactory 不能为空");
        this.parent = parent;

    }

    public String getName() {
        return name;
    }


    public void addResource(String path) throws Exception {
        resourceLoaderFactory.addResource(path);
    }

    public void addResource(File file) throws Exception {
        resourceLoaderFactory.addResource(file);
    }

    public void addResource(Path path) throws Exception {
        resourceLoaderFactory.addResource(path);
    }

    public void addResource(URL url) throws Exception {
        resourceLoaderFactory.addResource(url);
    }

    public void addResource(ResourceLoader resourceLoader) throws Exception{
        resourceLoaderFactory.addResource(resourceLoader);
    }

    public ClassLoader getParentClassLoader(){
        return parent;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(className)) {
            return findClass(className);
        }
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        Class<?> loadedClass = findClassFromParent(className);
        if (loadedClass != null) {
            return loadedClass;
        }
        loadedClass = findLoadedClass(className);
        if (loadedClass != null) {
            return loadedClass;
        }
        loadedClass = findClassFromLocal(className);
        if (loadedClass != null) {
            return loadedClass;
        }
        throw new ClassNotFoundException(className);
    }

    protected Class<?> findClassFromParent(String className) throws ClassNotFoundException{
        try {
            if(parent != null){
                return parent.loadClass(className);
            }
            return null;
        } catch (Exception e){
            return null;
        }
    }

    protected Class<?> findClassFromLocal(String name) {
        Class<?> aClass;
        String formatClassName = formatClassName(name);
        aClass = pluginClassCache.get(formatClassName);
        if (aClass != null) {
            return aClass;
        }
        Resource resource = resourceLoaderFactory.findResource(formatClassName);
        byte[] bytes = null;
        if(resource != null){
            bytes = resource.getBytes();
        }
        if(bytes == null || bytes.length == 0){
            bytes = getClassByte(formatClassName);
        }
        if(bytes == null || bytes.length == 0){
            return null;
        }
        aClass = defineClass(name, bytes, 0, bytes.length );
        if(aClass == null) {
            return null;
        }
        if (aClass.getPackage() == null) {
            int lastDotIndex = name.lastIndexOf( '.' );
            String packageName = (lastDotIndex >= 0) ? name.substring( 0, lastDotIndex) : "";
            definePackage(packageName, null, null, null,
                    null, null, null, null );
        }
        pluginClassCache.put(name, aClass);
        return aClass;
    }

    private byte[] getClassByte(String formatClassName){
        InputStream inputStream = resourceLoaderFactory.getInputStream(formatClassName);
        if(inputStream == null){
            return null;
        }
        try {
            return IOUtils.read(inputStream);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public URL[] getURLs() {
        List<Resource> resources = resourceLoaderFactory.getResources();
        URL[] urls = new URL[resources.size()];
        for (int i = 0; i < resources.size(); i++) {
            urls[i] = resources.get(i).getUrl();
        }
        return urls;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        name = formatResourceName(name);
        InputStream inputStream = findInputStreamFromParent(name);
        if(inputStream != null){
            return inputStream;
        }
        return findInputStreamFromLocal(name);
    }

    protected InputStream findInputStreamFromParent(String name){
        if(parent != null){
            return parent.getResourceAsStream(name);
        }
        return null;
    }

    protected InputStream findInputStreamFromLocal(String name){
        return resourceLoaderFactory.getInputStream(name);
    }

    @Override
    public URL getResource(String name) {
        name = formatResourceName(name);
        URL url = findResourceFromParent(name);
        if(url != null){
            return url;
        }
        return findResourceFromLocal(name);
    }

    protected URL findResourceFromParent(String name){
        if(parent != null){
            return parent.getResource(name);
        }
        return null;
    }

    protected URL findResourceFromLocal(String name) {
        Resource resource = resourceLoaderFactory.findResource(name);
        if (resource == null) {
            return null;
        }
        return resource.getUrl();
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        name = formatResourceName(name);
        Enumeration<URL> parentResources = findResourcesFromParent(name);
        Vector<URL> vector = new Vector<>();
        if(parentResources != null){
            while (parentResources.hasMoreElements()){
                URL url = parentResources.nextElement();
                vector.add(url);
            }
        }
        Enumeration<URL> localResources = findResourcesFromLocal(name);
        if(localResources != null){
            while (localResources.hasMoreElements()){
                URL url = localResources.nextElement();
                vector.add(url);
            }
        }
        return vector.elements();
    }

    protected Enumeration<URL> findResourcesFromParent(String name) throws IOException{
        if(parent != null){
            return parent.getResources(name);
        }
        return null;
    }

    protected Enumeration<URL> findResourcesFromLocal(String name) throws IOException{
        List<Resource> resourceList = resourceLoaderFactory.findResources(name);
        if(resourceList != null && !resourceList.isEmpty()){
            Vector<URL> vector = new Vector<>();
            for (Resource resource : resourceList) {
                vector.add(resource.getUrl());
            }
            return vector.elements();
        }
        return null;
    }

    private String formatClassName(String className) {
        className = className.replace( '/', '~' );
        className = className.replace( '.', '/' ) + ".class";
        className = className.replace( '~', '/' );
        return className;
    }

    private String formatResourceName(String resourceName){
        if(ObjectUtils.isEmpty(resourceName)) {
            return Resource.PACKAGE_SPLIT;
        }
        String[] split = resourceName.split("/");
        StringBuilder newPath = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if("".equals(s)){
                continue;
            }
            if(i == 0){
                newPath = new StringBuilder(s);
            } else {
                newPath.append(Resource.PACKAGE_SPLIT).append(s);
            }
        }
        if(resourceName.endsWith(Resource.PACKAGE_SPLIT)){
            newPath.append(Resource.PACKAGE_SPLIT);
        }
        return newPath.toString();
    }



    @Override
    public void close() throws IOException {
        synchronized (pluginClassCache){
            pluginClassCache.clear();
            IOUtils.closeQuietly(resourceLoaderFactory);
        }
    }


}
