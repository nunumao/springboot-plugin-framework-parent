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
