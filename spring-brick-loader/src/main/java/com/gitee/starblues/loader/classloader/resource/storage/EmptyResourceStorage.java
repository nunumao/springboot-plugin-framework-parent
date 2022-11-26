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

package com.gitee.starblues.loader.classloader.resource.storage;

import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.ResourceByteGetter;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * 空的资源存储
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.1.1
 */
public class EmptyResourceStorage implements ResourceStorage{
    @Override
    public void add(String name, URL url, ResourceByteGetter byteGetter) throws Exception {

    }

    @Override
    public void add(String name, URL url) throws Exception {

    }

    @Override
    public boolean exist(String name) {
        return false;
    }

    @Override
    public Resource get(String name) {
        return null;
    }

    @Override
    public InputStream getInputStream(String name) {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
