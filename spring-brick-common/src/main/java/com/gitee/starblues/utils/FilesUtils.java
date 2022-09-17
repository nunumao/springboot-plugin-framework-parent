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

package com.gitee.starblues.utils;

import com.gitee.starblues.common.Constants;
import com.gitee.starblues.common.PackageStructure;

import java.io.File;
import java.io.IOException;

/**
 * 文件工具类
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.1.0
 */
public class FilesUtils {

    /**
     * 正斜杠
     */
    public static final String SLASH = "/";

    /**
     * 双正斜杠
     */
    public static final String DOUBLE_SLASH = "//";

    /**
     * 反斜杠
     */
    public static final String  BACKSLASH = "\\";


    /**
     * 获取存在的文件
     *
     * @param pathStr 文件路径
     * @return File
     */
    public static File getExistFile(String pathStr){
        File file = new File(pathStr);
        if(file.exists()){
            return file;
        }
        return null;
    }

    /**
     * 是否存在文件
     * @param path 文件路径
     * @return boolean
     */
    public static boolean existFile(String path){
        if(ObjectUtils.isEmpty(path)){
            return false;
        }
        return new File(path).exists();
    }


    /**
     * 拼接file路径
     *
     * @param paths 拼接的路径
     * @return 拼接的路径
     * @since 3.0.0
     */
    public static String joiningFilePath(String ...paths) {
        if (paths == null || paths.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int length = paths.length;
        for (int i = 0; i < length; i++) {
            String path = paths[i];
            if (ObjectUtils.isEmpty(path)) {
                continue;
            }
            if (i < length - 1) {
                if (path.endsWith(SLASH)) {
                    path = path.replace(SLASH, "");
                } else if (path.endsWith(BACKSLASH)) {
                    path = path.replace(BACKSLASH, "");
                } else if (path.endsWith(DOUBLE_SLASH)) {
                    path = path.replace(DOUBLE_SLASH, "");
                }
            }
            if (i > 0) {
                if (path.startsWith(File.separator) || path.startsWith(SLASH) ||
                        path.startsWith(DOUBLE_SLASH) || path.startsWith(BACKSLASH)) {
                    stringBuilder.append(path);
                } else {
                    stringBuilder.append(File.separator).append(path);
                }
            } else {
                stringBuilder.append(path);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 拼接 zip /jar 路径
     *
     * @param paths 拼接的路径
     * @return 拼接的路径
     * @since 3.1.0
     */
    public static String joiningZipPath(String ...paths){
        if(paths == null || paths.length == 0){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int length = paths.length;
        for (int i = 0; i < length; i++) {
            String path = paths[i];
            if(ObjectUtils.isEmpty(path)) {
                continue;
            }
            if(i < length - 1){
                if(path.endsWith(SLASH)){
                    path = path.replace(SLASH, "");
                } else if(path.endsWith(BACKSLASH)){
                    path = path.replace(BACKSLASH, "");
                } else if(path.endsWith(DOUBLE_SLASH)){
                    path = path.replace(DOUBLE_SLASH, "");
                }
            }
            if(i > 0){
                if(path.startsWith(PackageStructure.SEPARATOR)){
                    stringBuilder.append(path);
                } else {
                    stringBuilder.append(PackageStructure.SEPARATOR).append(path);
                }
            } else {
                stringBuilder.append(path);
            }
        }

        return stringBuilder.toString();
    }

    public static File createFile(String path) throws IOException {
        try {
            File file = new File(path);
            if(file.exists()){
                return file;
            }
            File parentFile = file.getParentFile();
            if(!parentFile.exists()){
                if(!parentFile.mkdirs()){
                    throw new IOException("Create " + parentFile + " dir error");
                }
            }
            if(file.createNewFile()){
                return file;
            }
            throw new IOException("Create " + path + " file error");
        } catch (Exception e){
            throw new IOException("Create " + path + " file error");
        }
    }

    /**
     * 解决相对路径
     * @param rootPath 根路径
     * @param relativePath 以 ~ 开头的相对路径
     * @return 处理后的路径
     */
    public static String resolveRelativePath(String rootPath, String relativePath){
        if(ObjectUtils.isEmpty(relativePath)){
            return relativePath;
        }
        if(isRelativePath(relativePath)){
            String resolveRelativePath = relativePath.replaceFirst(Constants.RELATIVE_SIGN, "");
            return joiningFilePath(rootPath, resolveRelativePath);
        } else {
            return relativePath;
        }
    }

    /**
     * 解决存在的相对路径
     * @param rootPath 根路径
     * @param path 以 ~ 开头的相对路径或者完整路径
     * @return File 或者 null(不存在)
     */
    public static File resolveExistRelativePathFile(String rootPath, String path){
        if(ObjectUtils.isEmpty(path)){
            return null;
        }
        if(isRelativePath(path)){
            String resolveRelativePath = path.replaceFirst(Constants.RELATIVE_SIGN, "");
            String joiningFilePath = joiningFilePath(rootPath, resolveRelativePath);
            return getExistFile(joiningFilePath);
        } else {
            File existFile = getExistFile(path);
            if(existFile != null){
                return existFile;
            }
            String joiningFilePath = joiningFilePath(rootPath, path);
            return getExistFile(joiningFilePath);
        }
    }

    /**
     * 是否是相对路径
     * @param path 路径
     * @return true 为相对路径, false 未非相对路径
     */
    public static boolean isRelativePath(String path){
        if(ObjectUtils.isEmpty(path)){
            return false;
        }
        return path.startsWith(Constants.RELATIVE_SIGN);
    }


}
