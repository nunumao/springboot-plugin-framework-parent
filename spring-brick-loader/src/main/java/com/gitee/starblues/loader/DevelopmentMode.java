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
     * 简单模式
     */
    SIMPLE("simple"),

    /**
     * 动态模式
     */
    DYNAMIC("dynamic");

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
        if(SIMPLE.name().equalsIgnoreCase(model)){
            return DevelopmentMode.SIMPLE;
        } else {
            return DevelopmentMode.DYNAMIC;
        }
    }
}
