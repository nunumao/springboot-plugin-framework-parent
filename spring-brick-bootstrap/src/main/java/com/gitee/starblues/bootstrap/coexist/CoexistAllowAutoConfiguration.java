package com.gitee.starblues.bootstrap.coexist;

import com.gitee.starblues.utils.ObjectUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Coexist模式下存储当前插件允许的 auto 配置
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
public class CoexistAllowAutoConfiguration {

    private final Set<String> allowPrefix = new HashSet<>();

    public CoexistAllowAutoConfiguration(){
        addDefault();
    }

    private void addDefault(){
        allowPrefix.add("org.springframework.boot.autoconfigure.aop.AopAutoConfiguration");
    }

    public CoexistAllowAutoConfiguration add(String autoConfigurationClass){
        if(ObjectUtils.isEmpty(autoConfigurationClass)){
            return this;
        }
        allowPrefix.add(autoConfigurationClass);
        return this;
    }

    public boolean match(String autoConfigurationClass){
        for (String prefix : allowPrefix) {
            if(autoConfigurationClass.startsWith(prefix)){
                return true;
            }
        }
        return false;
    }

}
