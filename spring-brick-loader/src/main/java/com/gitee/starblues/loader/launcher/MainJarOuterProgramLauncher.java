/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.loader.launcher;

import com.gitee.starblues.loader.archive.Archive;
import com.gitee.starblues.loader.archive.ExplodedArchive;
import com.gitee.starblues.loader.archive.JarFileArchive;
import com.gitee.starblues.loader.classloader.GenericClassLoader;
import com.gitee.starblues.loader.classloader.resource.loader.MainJarResourceLoader;
import com.gitee.starblues.loader.launcher.runner.MethodRunner;
import com.gitee.starblues.loader.utils.FilesUtils;
import com.gitee.starblues.loader.utils.ObjectUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.Manifest;

import static com.gitee.starblues.loader.LoaderConstant.*;


/**
 * 主程序jar-outer 模式启动者
 *
 * @author starBlues
 * @version 3.0.2
 */
public class MainJarOuterProgramLauncher extends MainProgramLauncher{


    private final static Archive.EntryFilter ENTRY_FILTER = (entry)->{
        String name = entry.getName();
        return name.startsWith(PROD_CLASSES_PATH);
    };

    private final static Archive.EntryFilter INCLUDE_FILTER = (entry) -> {
        if (entry.isDirectory()) {
            return entry.getName().equals(PROD_CLASSES_PATH);
        }
        return false;
    };

    private final File rootJarFile;

    public MainJarOuterProgramLauncher(MethodRunner methodRunner, File rootJarFile) {
        super(methodRunner);
        this.rootJarFile = Objects.requireNonNull(rootJarFile, "参数 rootJarFile 不能为空");
    }

    @Override
    protected boolean resolveThreadClassLoader() {
        return true;
    }

    @Override
    protected void addResource(GenericClassLoader classLoader) throws Exception {
        super.addResource(classLoader);
        Archive archive = getArchive();
        Iterator<Archive> archiveIterator = archive.getNestedArchives(ENTRY_FILTER, INCLUDE_FILTER);
        addEntryResource(archiveIterator, classLoader);
        addLibResource(archive, classLoader);
    }

    private Archive getArchive() throws IOException {
        return (rootJarFile.isDirectory() ? new ExplodedArchive(rootJarFile) : new JarFileArchive(rootJarFile));
    }

    private void addEntryResource(Iterator<Archive> archives, GenericClassLoader classLoader) throws Exception {
        while (archives.hasNext()){
            Archive archive = archives.next();
            URL url = archive.getUrl();
            String path = url.getPath();
            if(path.contains(PROD_CLASSES_URL_SIGN)){
                classLoader.addResource(new MainJarResourceLoader(url));
            }
        }
    }

    private void addLibResource(Archive archive, GenericClassLoader classLoader) throws Exception {
        Manifest manifest = archive.getManifest();
        String libDir = manifest.getMainAttributes().getValue(MAIN_LIB_DIR);
        String relativePath = rootJarFile.isDirectory() ? rootJarFile.getPath() : rootJarFile.getParent();
        libDir = FilesUtils.resolveRelativePath(relativePath, libDir);
        File libJarDir = new File(libDir);
        if(libJarDir.exists()){
            List<String> libIndexes = getLibIndexes(manifest);
            addLibJarFile(libJarDir, libIndexes, classLoader);
        } else {
            throw new IllegalStateException("主程序依赖目录不存在: " + libDir);
        }
    }

    private List<String> getLibIndexes(Manifest manifest){
        String libIndexes = manifest.getMainAttributes().getValue(MAIN_LIB_INDEXES);
        if(ObjectUtils.isEmpty(libIndexes)){
            return Collections.emptyList();
        }
        String[] indexSplit = libIndexes.split(MAIN_LIB_INDEXES_SPLIT);
        List<String> indexes = new ArrayList<>(indexSplit.length);
        for (String index : indexSplit) {
            if(ObjectUtils.isEmpty(index)){
                continue;
            }
            indexes.add(index);
        }
        if(indexes.isEmpty()){
            throw new IllegalStateException("主程序依赖包未发现!");
        }
        return indexes;
    }

    private void addLibJarFile(File rootFile, List<String> libIndexes, GenericClassLoader classLoader) throws Exception {
        Set<String> linIndexes = new HashSet<>(libIndexes);
        File[] listFiles = rootFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return linIndexes.contains(pathname.getName());
            }
        });
        if(listFiles == null || listFiles.length == 0){
            return;
        }
        for (File listFile : listFiles) {
            classLoader.addResource(listFile);
        }
    }



}
