package com.gitee.starblues.loader.launcher;

import com.gitee.starblues.loader.DevelopmentMode;

import java.util.Objects;

/**
 * DevelopmentMode 设置者
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
public class DevelopmentModeSetting {

    private static DevelopmentMode developmentMode;

    static void setDevelopmentMode(DevelopmentMode developmentMode) {
        DevelopmentModeSetting.developmentMode = developmentMode;
    }

    public static boolean isolation(){
        return Objects.equals(developmentMode, DevelopmentMode.ISOLATION);
    }

    public static boolean coexist(){
        return Objects.equals(developmentMode, DevelopmentMode.COEXIST);
    }

    public static boolean simple(){
        return Objects.equals(developmentMode, DevelopmentMode.SIMPLE);
    }

    public static DevelopmentMode getDevelopmentMode(){
        return developmentMode;
    }

}
