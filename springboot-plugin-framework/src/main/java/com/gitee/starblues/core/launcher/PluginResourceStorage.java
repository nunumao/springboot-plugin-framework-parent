package com.gitee.starblues.core.launcher;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.launcher.jar.AbstractJarFile;
import com.gitee.starblues.core.launcher.jar.JarFile;
import com.gitee.starblues.core.launcher.jar.JarFileWrapper;
import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginResourceStorage {

    public static Map<String, Storage> pluginResourceStorage = new ConcurrentHashMap<>();


    public static void addPlugin(InsidePluginDescriptor descriptor){
        if(pluginResourceStorage.containsKey(descriptor.getPluginId())){
            return;
        }
        pluginResourceStorage.put(descriptor.getPluginId(), new Storage(descriptor));
    }


    public static void removePlugin(String pluginId){
        Storage storage = pluginResourceStorage.get(pluginId);
        if(storage == null){
            return;
        }
        IOUtils.closeQuietly(storage);
        pluginResourceStorage.remove(pluginId);
    }

    public static void addJarFile(AbstractJarFile jarFile){
        pluginResourceStorage.forEach((k,v)->{
            v.addJarFile(jarFile.getName(), jarFile);
        });
    }

    public static void addRootJarFile(File file, JarFile jarFile){
        pluginResourceStorage.forEach((k,v)->{
            v.addRootJarFile(file, jarFile);
        });
    }

    public static JarFile getRootJarFile(File file){
        for (Storage value : pluginResourceStorage.values()) {
            JarFile jarFile = value.getRootJarFile(file);
            if(jarFile != null){
                return jarFile;
            }
        }
        return null;
    }


    private static class Storage implements Closeable {
        private final InsidePluginDescriptor descriptor;
        private final Map<File, JarFile> rootJarFileMap = new ConcurrentHashMap<>();
        private final Map<String, List<AbstractJarFile>> jarFileMap = new ConcurrentHashMap<>();

        public Storage(InsidePluginDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        public void addJarFile(String name, AbstractJarFile jarFile){
            if(name == null || jarFile == null){
                return;
            }
            String pluginFileName = descriptor.getPluginFileName();
            if(name.contains(pluginFileName)){
                List<AbstractJarFile> jarFiles = jarFileMap.computeIfAbsent(name, k -> new ArrayList<>());
                jarFiles.add(jarFile);
            }
        }

        public void addRootJarFile(File file, JarFile jarFile){
            String pluginFileName = descriptor.getPluginFileName();
            String absolutePath = file.getAbsolutePath();
            if(absolutePath.contains(pluginFileName)){
                rootJarFileMap.put(file, jarFile);
            }
        }

        public JarFile getRootJarFile(File file){
            return rootJarFileMap.get(file);
        }

        @Override
        public void close() throws IOException {
            for (List<AbstractJarFile> value : jarFileMap.values()) {
                for (AbstractJarFile jarFile : value) {
                    if(jarFile == null){
                        continue;
                    }
                    if(jarFile instanceof JarFileWrapper){
                        ((JarFileWrapper)jarFile).canClosed();
                    }
                    IOUtils.closeQuietly(jarFile);
                }
            }
            jarFileMap.clear();
            rootJarFileMap.clear();
        }
    }


}
