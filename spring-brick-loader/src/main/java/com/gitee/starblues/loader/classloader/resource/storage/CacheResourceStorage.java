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

import com.gitee.starblues.loader.classloader.resource.loader.DefaultResource;
import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.ResourceByteGetter;
import com.gitee.starblues.loader.utils.IOUtils;
import com.gitee.starblues.loader.utils.ObjectUtils;
import com.gitee.starblues.loader.utils.ResourceUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的资源存储者
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.1.1
 */
public class CacheResourceStorage extends SameRootResourceStorage{

    protected final Map<String, Resource> resourceStorage = new ConcurrentHashMap<>();

    private final List<InputStream> inputStreams = new ArrayList<>();

    public CacheResourceStorage(URL baseUrl) {
        super(baseUrl);
    }

    @Override
    public void add(String name, URL url, ResourceByteGetter byteGetter) throws Exception{
        name = formatResourceName(name);
        if(resourceStorage.containsKey(name)){
            return;
        }
        CacheResource cacheResource = new CacheResource(name, baseUrl, url);
        cacheResource.setBytes(byteGetter);
        addResource(name, cacheResource);
    }

    @Override
    public void add(String name, URL url) throws Exception{
        if(ObjectUtils.isEmpty(name) || url == null){
            return;
        }
        this.add(name, url, null);
    }

    @Override
    public boolean exist(String name) {
        if(ObjectUtils.isEmpty(name)){
            return false;
        }
        name = formatResourceName(name);
        return resourceStorage.containsKey(name);
    }

    protected void addResource(String name, Resource resource){
        if(ObjectUtils.isEmpty(name) || resource == null){
            return;
        }
        resourceStorage.put(name, resource);
    }

    @Override
    public Resource get(String name) {
        if(ObjectUtils.isEmpty(name)){
            return null;
        }
        name = formatResourceName(name);
        return resourceStorage.get(name);
    }

    @Override
    public InputStream getInputStream(String name) {
        if(ObjectUtils.isEmpty(name)){
            return null;
        }
        Resource resource = get(name);
        if(resource == null){
            return null;
        }
        try {
            InputStream inputStream = resource.getUrl().openStream();
            inputStreams.add(inputStream);
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        for (InputStream inputStream : inputStreams) {
            IOUtils.closeQuietly(inputStream);
        }
        for (Resource resource : resourceStorage.values()) {
            IOUtils.closeQuietly(resource);
        }
        resourceStorage.clear();
    }

    protected final String formatResourceName(String name) {
        return ResourceUtils.formatStandardName(name);
    }

    /**
     * 缓存资源
     */
    protected static class CacheResource extends DefaultResource {

        protected byte[] bytes;

        public CacheResource(String name, URL baseUrl, URL url) {
            super(name, baseUrl, url);
        }

        @Override
        public void setBytes(ResourceByteGetter byteGetter) throws Exception{
            if(byteGetter == null){
                return;
            }
            bytes = byteGetter.get();
        }

        @Override
        public byte[] getBytes() {
            return bytes;
        }
    }

}
