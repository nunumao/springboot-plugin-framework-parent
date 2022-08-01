/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.loader.launcher;

import com.gitee.starblues.loader.DevelopmentMode;

/**
 * DevelopmentMode 设置者
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.0.4
 */
public class DevelopmentModeSetting {

    private static String developmentMode = DevelopmentMode.ISOLATION;

    static void setDevelopmentMode(String developmentMode) {
        DevelopmentModeSetting.developmentMode = checkModeKey(developmentMode);
    }

    public static boolean isolation(){
        return DevelopmentMode.ISOLATION.equalsIgnoreCase(developmentMode);
    }

    public static boolean coexist(){
        return DevelopmentMode.COEXIST.equalsIgnoreCase(developmentMode);
    }

    public static boolean simple(){
        return DevelopmentMode.SIMPLE.equalsIgnoreCase(developmentMode);
    }

    public static String getDevelopmentMode(){
        return developmentMode;
    }

    private static String checkModeKey(String developmentMode){
        if(developmentMode == null || "".equals(developmentMode)){
            throw new RuntimeException("developmentMode设置不能为空");
        }
        if(DevelopmentMode.ISOLATION.equalsIgnoreCase(developmentMode)){
            return developmentMode;
        } else if(DevelopmentMode.COEXIST.equalsIgnoreCase(developmentMode)){
            return developmentMode;
        } else if(DevelopmentMode.SIMPLE.equalsIgnoreCase(developmentMode)){
            return developmentMode;
        } else {
            throw new RuntimeException("不支持开发模式: " + developmentMode);
        }
    }

}
