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

package com.gitee.starblues.loader.utils;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 资源工具
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.0
 */
public class ResourceUtils {

    public static final String URL_PROTOCOL_FILE = "file";
    public static final String URL_PROTOCOL_JAR_FILE = "jar";
    public static final String JAR_FILE_EXTENSION = ".jar";
    public static final String ZIP_FILE_EXTENSION = ".zip";

    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
    public static final String URL_PROTOCOL_VFS = "vfs";

    public static final String PACKAGE_SPLIT = "/";

    private ResourceUtils(){}

    /**
     * 是否为jar文件
     * @param url url
     * @return boolean
     */
    public static boolean isJarFileUrl(URL url) {
        String protocol = url.getProtocol();
        boolean extensionIsJar = url.getPath().toLowerCase().endsWith(JAR_FILE_EXTENSION);
        return (URL_PROTOCOL_FILE.equals(protocol) && extensionIsJar)
                || (URL_PROTOCOL_JAR_FILE.equals(protocol) || extensionIsJar);
    }

    /**
     * 是否为jar文件
     * @param file file
     * @return boolean
     */
    public static boolean isJarFile(File file) {
        return file.getName().toLowerCase().endsWith(JAR_FILE_EXTENSION);
    }

    /**
     * 是否为zip文件
     * @param url url
     * @return boolean
     */
    public static boolean isZipFileUrl(URL url) {
        String protocol = url.getProtocol();
        boolean extensionIsZip = url.getPath().toLowerCase().endsWith(ZIP_FILE_EXTENSION);
        return (URL_PROTOCOL_FILE.equals(protocol) && extensionIsZip);
    }

    /**
     * 是否为jar协议的文件
     * @param url url
     * @return boolean
     */
    public static boolean isJarProtocolUrl(URL url) {
        return URL_PROTOCOL_JAR_FILE.equals(url.getProtocol());
    }


    /**
     * 是否为普通文件
     * @param url url
     * @return boolean
     */
    public static boolean isFileUrl(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_FILE.equals(protocol) || URL_PROTOCOL_VFSFILE.equals(protocol) ||
                URL_PROTOCOL_VFS.equals(protocol));
    }

    /**
     * 将资源名称统一格式化为标准格式
     * 标准格式为 a/b/c
     * @param name 原始资源名称
     * @return 标准资源名称
     */
    public static String formatStandardName(String name){
        if(ObjectUtils.isEmpty(name)) {
            return PACKAGE_SPLIT;
        }
        String[] split = name.split(PACKAGE_SPLIT);
        StringBuilder newPath = null;
        for (String s : split) {
            if ("".equals(s)) {
                continue;
            }
            if (newPath == null) {
                newPath = new StringBuilder(s);
            } else {
                newPath.append(PACKAGE_SPLIT).append(s);
            }
        }
        if(newPath == null || newPath.length() == 0){
            return PACKAGE_SPLIT;
        }
        return newPath.toString();
    }

    /**
     * 释放资源
     * @param object 释放资源的对象
     */
    public static void release(final Object object) {
        release(object, null);
    }

    /**
     * 释放资源
     * @param object 释放资源的对象
     * @param consumer 释放异常消费
     */
    public static void release(final Object object, final Consumer<Exception> consumer) {
        if (object instanceof Release) {
            try {
                ((Release)object).release();
            } catch (final Exception e) {
                if (consumer != null) {
                    consumer.accept(e);
                }
            }
        }
    }

}
