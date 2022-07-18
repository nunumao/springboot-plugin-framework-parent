package com.gitee.starblues.loader.launcher;

import com.gitee.starblues.loader.launcher.coexist.CoexistBaseLauncher;
import com.gitee.starblues.loader.launcher.isolation.IsolationBaseLauncher;
import com.gitee.starblues.loader.launcher.runner.MethodRunner;
import com.gitee.starblues.loader.launcher.simple.SimpleBaseLauncher;
import lombok.AllArgsConstructor;

/**
 * 开发环境 Launcher
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
@AllArgsConstructor
public class DevLauncher implements Launcher<ClassLoader>{



    private final SpringBootstrap springBootstrap;

    @Override
    public ClassLoader run(String... args) throws Exception {
        MethodRunner methodRunner = new MethodRunner(springBootstrap.getClass().getName(),
                SPRING_BOOTSTRAP_RUN_METHOD, args);
        AbstractMainLauncher launcher;
        if(DevelopmentModeSetting.coexist()){
            launcher = new CoexistBaseLauncher(methodRunner);
        } else if(DevelopmentModeSetting.simple()) {
            launcher = new SimpleBaseLauncher(methodRunner);
        } else {
            launcher = new IsolationBaseLauncher(methodRunner);
        }
        return launcher.run(args);
    }
}
