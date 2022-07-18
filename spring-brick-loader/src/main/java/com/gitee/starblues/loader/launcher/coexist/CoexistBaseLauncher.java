package com.gitee.starblues.loader.launcher.coexist;

import com.gitee.starblues.loader.classloader.GeneralUrlClassLoader;
import com.gitee.starblues.loader.classloader.GenericClassLoader;
import com.gitee.starblues.loader.launcher.AbstractMainLauncher;
import com.gitee.starblues.loader.launcher.isolation.IsolationBaseLauncher;
import com.gitee.starblues.loader.launcher.runner.MethodRunner;


/**
 * coexist 模式 launcher
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
public class CoexistBaseLauncher extends AbstractMainLauncher {

    private final MethodRunner methodRunner;

    public CoexistBaseLauncher(MethodRunner methodRunner) {
        this.methodRunner = methodRunner;
    }

    @Override
    protected ClassLoader createClassLoader(String... args) throws Exception {
        GeneralUrlClassLoader urlClassLoader = new GeneralUrlClassLoader(MAIN_CLASS_LOADER_NAME,
                this.getClass().getClassLoader());
        addResource(urlClassLoader);
        return urlClassLoader;
    }

    @Override
    protected ClassLoader launch(ClassLoader classLoader, String... args) throws Exception {
        methodRunner.run(classLoader);
        return classLoader;
    }

    protected void addResource(GeneralUrlClassLoader classLoader) throws Exception{

    }

}
