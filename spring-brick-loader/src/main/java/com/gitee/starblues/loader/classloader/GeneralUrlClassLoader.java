package com.gitee.starblues.loader.classloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 通用的Url ClassLoader
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
public class GeneralUrlClassLoader extends URLClassLoader {

    public GeneralUrlClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }

    public void addUrl(String url) throws Exception{
        addPath(Paths.get(url));
    }

    public void addPath(Path path) throws Exception{
        addFile(path.toFile());
    }

    public void addFile(File file) throws Exception {
        if(!file.exists()){
            throw new FileNotFoundException("Not found file:" + file.getPath());
        }
        addURL(file.toPath().toUri().toURL());
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
