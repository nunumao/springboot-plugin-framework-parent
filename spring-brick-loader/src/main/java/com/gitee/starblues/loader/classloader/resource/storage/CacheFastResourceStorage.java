/**
 * Copyright [2019-Present] [starBlues]
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
import com.gitee.starblues.loader.classloader.resource.cache.Cache;
import com.gitee.starblues.loader.classloader.resource.cache.DefaultCacheExpirationTrigger;
import com.gitee.starblues.loader.classloader.resource.cache.LRUMapCache;
import com.gitee.starblues.loader.utils.IOUtils;
import com.gitee.starblues.loader.utils.ObjectUtils;
import com.gitee.starblues.loader.utils.ResourceUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 快速且可缓存的资源存储者
 * 优点: 释放前速度比较快, 释放后可根据LRU缓存机制进行缓存
 * 缺点: 释放前占用内存比较高
 *
 * @author starBlues
 * @since 3.1.1
 * @version 3.1.1
 */
public class CacheFastResourceStorage extends AbstractResourceStorage {

    protected final Cache<String, List<Resource>> resourceStorage;
    private final ResourceStorage cacheResourceStorage;

    private final List<InputStream> inputStreams = new ArrayList<>();

    private volatile boolean release = false;

    public CacheFastResourceStorage(String key) {
        this.cacheResourceStorage = new CachePerpetualResourceStorage();
        // 最大 1000 个, 最长 3 分钟
        resourceStorage = DefaultCacheExpirationTrigger.getCacheExpirationTrigger(3, TimeUnit.MINUTES)
                .getCache(key, () -> new LRUMapCache<String, List<Resource>>(1000, 180000));
    }

    @Override
    protected void addResource(Resource resource) throws Exception {
        if(!release){
            cacheResourceStorage.add(resource);
            return;
        }
        resource.resolveByte();
        String name = formatResourceName(resource.getName());
        // TODO 多缓存处理
        List<Resource> resources = resourceStorage.getOrDefault(name, ArrayList::new, true);
        resources.add(resource);
    }

    @Override
    public boolean exist(String name) {
        if(!release){
            return cacheResourceStorage.exist(name);
        }
        return getFirst(name) != null;
    }

    @Override
    public Resource getFirst(String name) {
        if(!release){
            return cacheResourceStorage.getFirst(name);
        }
        if(ObjectUtils.isEmpty(name)){
            return null;
        }
        name = formatResourceName(name);
        List<Resource> resources = resourceStorage.get(name);
        if(!ObjectUtils.isEmpty(resources)){
            return resources.get(0);
        }
        return searchResource(name);
    }

    @Override
    public Enumeration<Resource> get(String name) {
        if(!release){
            return cacheResourceStorage.get(name);
        }
        if(ObjectUtils.isEmpty(name)){
            return Collections.emptyEnumeration();
        }
        name = formatResourceName(name);
        List<Resource> resources = resourceStorage.get(name);
        if(!ObjectUtils.isEmpty(resources)){
            return Collections.enumeration(resources);
        }
        return searchResources(name);
    }

    @Override
    public InputStream getFirstInputStream(String name) {
        Resource resource = getFirst(name);
        return openStream(resource);
    }

    @Override
    public Enumeration<InputStream> getInputStream(String name) {
        if(!release){
            return cacheResourceStorage.getInputStream(name);
        }
        Enumeration<Resource> resources = get(name);
        if(!ObjectUtils.isEmpty(resources)){
            return openStream(resources);
        }
        Enumeration<Resource> searchResources = searchResources(name);
        return openStream(searchResources);
    }

    @Override
    public synchronized void release() throws Exception {
        if(!release){
            IOUtils.closeQuietly(cacheResourceStorage);
        }
        resourceStorage.cleanExpired();
        release = true;
    }

    @Override
    public void close() throws Exception {
        resourceStorage.clear(ResourceUtils::release);
        for (InputStream inputStream : inputStreams) {
            IOUtils.closeQuietly(inputStream);
        }
        inputStreams.clear();
    }


}
