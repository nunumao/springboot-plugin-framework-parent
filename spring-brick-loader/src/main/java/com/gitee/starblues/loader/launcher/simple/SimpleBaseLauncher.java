package com.gitee.starblues.loader.launcher.simple;

import com.gitee.starblues.loader.classloader.GeneralUrlClassLoader;
import com.gitee.starblues.loader.launcher.AbstractMainLauncher;
import com.gitee.starblues.loader.launcher.isolation.IsolationBaseLauncher;
import com.gitee.starblues.loader.launcher.runner.MethodRunner;


/**
 * simple 模式 launcher
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
public class SimpleBaseLauncher extends AbstractMainLauncher {

    private final MethodRunner methodRunner;

    public SimpleBaseLauncher(MethodRunner methodRunner) {
        this.methodRunner = methodRunner;
    }

    @Override
    protected ClassLoader createClassLoader(String... args) throws Exception {
        return new GeneralUrlClassLoader(IsolationBaseLauncher.MAIN_CLASS_LOADER_NAME, this.getClass().getClassLoader());
    }

    @Override
    protected ClassLoader launch(ClassLoader classLoader, String... args) throws Exception {
        methodRunner.run(classLoader);
        return classLoader;
    }
}
