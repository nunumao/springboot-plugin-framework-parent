package com.gitee.starblues.loader.launcher.classpath;

import com.gitee.starblues.loader.archive.Archive;
import com.gitee.starblues.loader.archive.ExplodedArchive;
import com.gitee.starblues.loader.archive.JarFileArchive;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.gitee.starblues.loader.LoaderConstant.*;

/**
 * fast jar 类型的 classpath 获取者
 *
 * @author starBlues
 * @version 3.0.4
 * @since 3.0.4
 */
@AllArgsConstructor
public class FastJarClasspathResource implements ClasspathResource{

    private final static Archive.EntryFilter ENTRY_FILTER = (entry)->{
        String name = entry.getName();
        return name.startsWith(PROD_CLASSES_PATH) || name.startsWith(PROD_LIB_PATH);
    };

    private final static Archive.EntryFilter INCLUDE_FILTER = (entry) -> {
        if (entry.isDirectory()) {
            return entry.getName().equals(PROD_CLASSES_PATH);
        }
        return entry.getName().startsWith(PROD_LIB_PATH);
    };

    private final File rootJarFile;

    @Override
    public List<URL> getClasspath() throws Exception{
        Archive archive = getArchive();
        Iterator<Archive> archiveIterator = archive.getNestedArchives(ENTRY_FILTER, INCLUDE_FILTER);
        List<URL> urls = new ArrayList<>();
        while (archiveIterator.hasNext()){
            URL url = archiveIterator.next().getUrl();
            urls.add(url);
        }
        return urls;
    }

    private Archive getArchive() throws IOException {
        return (rootJarFile.isDirectory() ? new ExplodedArchive(rootJarFile) : new JarFileArchive(rootJarFile));
    }

}
