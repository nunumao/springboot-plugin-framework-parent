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
import com.gitee.starblues.loader.classloader.resource.loader.DefaultResource;
import com.gitee.starblues.loader.classloader.resource.ResourceByteGetter;
import com.gitee.starblues.loader.utils.ObjectUtils;
import com.gitee.starblues.loader.utils.ResourceUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * 可缓存的资源存储者
 *
 * @author starBlues
 * @since 3.1.1
 * @version 3.1.1
 */
public class CacheReleasedResourceStorage extends CacheResourceStorage {

    private volatile boolean release = false;

    public CacheReleasedResourceStorage(URL baseUrl) {
        super(baseUrl);
    }

    @Override
    public void add(String name, URL url, ResourceByteGetter byteGetter) throws Exception{
        name = formatResourceName(name);
        if(resourceStorage.containsKey(name)){
            return;
        }
        CacheReleasedResource cacheResource = new CacheReleasedResource(name, baseUrl, url);
        cacheResource.setBytes(byteGetter);
        addResource(name, cacheResource);
    }

    @Override
    public boolean exist(String name) {
        return get(name) != null;
    }

    @Override
    public Resource get(String name) {
        Resource resource = super.get(name);
        if(resource != null){
            return resource;
        }
        if(!release){
            return null;
        }
        URL existUrl = getExistUrl(name);
        if(existUrl == null){
            return null;
        }
        DefaultResource defaultResource = new DefaultResource(name, baseUrl, existUrl);
        addResource(name, defaultResource);
        return defaultResource;
    }


    @Override
    public void release() throws Exception {
        for (Resource resource : resourceStorage.values()) {
            ResourceUtils.release(resource);
        }
        resourceStorage.clear();
        release = true;
    }

    /**
     * 获取存在的url
     * @param name 资源名称
     * @return 存在的URL, 不存在返回nulll
     */
    private URL getExistUrl(String name){
        URL url;
        try {
            url = new URL(baseUrl, name);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("非法：" + name);
        }
        try {
            URLConnection uc = url.openConnection();
            if (uc instanceof HttpURLConnection) {
                HttpURLConnection hconn = (HttpURLConnection)uc;
                hconn.setRequestMethod("HEAD");
                if (hconn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    return null;
                }
            } else {
                uc.setUseCaches(false);
                InputStream is = uc.getInputStream();
                is.close();
            }
            return url;
        } catch (Exception e) {
            return null;
        }
    }

    protected static class CacheReleasedResource extends CacheResourceStorage.CacheResource{

        public CacheReleasedResource(String name, URL baseUrl, URL url) {
            super(name, baseUrl, url);
        }

        @Override
        public void release() {
            Arrays.fill(bytes, (byte) 0);
            bytes = null;
        }
    }


}
