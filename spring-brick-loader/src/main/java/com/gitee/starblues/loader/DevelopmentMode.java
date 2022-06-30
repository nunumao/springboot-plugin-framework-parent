package com.gitee.starblues.loader;

/**
 * 插件开发模式
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
public enum DevelopmentMode {

    /**
     * 隔离模式
     */
    ISOLATION("isolation"),

    /**
     * 共存模式
     */
    COEXIST("coexist"),

    /**
     * 简单模式
     */
    SIMPLE("simple");

    private final String developmentMode;

    DevelopmentMode(String developmentMode) {
        this.developmentMode = developmentMode;
    }

    public String getDevelopmentMode() {
        return developmentMode;
    }

    @Override
    public String toString() {
        return developmentMode;
    }

    public static DevelopmentMode byName(String model){
        if(COEXIST.getDevelopmentMode().equalsIgnoreCase(model)){
            return DevelopmentMode.ISOLATION;
        } else if(SIMPLE.getDevelopmentMode().equalsIgnoreCase(model)){
            return DevelopmentMode.SIMPLE;
        } else {
            return DevelopmentMode.ISOLATION;
        }
    }
}
