package com.gitee.starblues.loader.launcher;

import com.gitee.starblues.loader.classloader.GeneralUrlClassLoader;
import com.gitee.starblues.loader.launcher.runner.MethodRunner;


/**
 * @author starBlues
 * @since 3.0.4
 */
public class SimpleModeMainLauncher extends AbstractMainLauncher<ClassLoader>{

    private final MethodRunner methodRunner;

    public SimpleModeMainLauncher(MethodRunner methodRunner) {
        this.methodRunner = methodRunner;
    }

    @Override
    protected ClassLoader createClassLoader(String... args) throws Exception {
        return new GeneralUrlClassLoader(this.getClass().getClassLoader());
    }

    @Override
    protected ClassLoader launch(ClassLoader classLoader, String... args) throws Exception {
        methodRunner.run(classLoader);
        return classLoader;
    }
}
