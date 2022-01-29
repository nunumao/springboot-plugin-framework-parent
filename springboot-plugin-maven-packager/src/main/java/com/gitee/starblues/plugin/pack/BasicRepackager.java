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

package com.gitee.starblues.plugin.pack;

import com.gitee.starblues.common.AbstractDependencyPlugin;
import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.plugin.pack.dev.DevConfig;
import com.gitee.starblues.plugin.pack.utils.CommonUtils;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PackageStructure.*;
import static com.gitee.starblues.common.PluginDescriptorKey.*;
import static com.gitee.starblues.plugin.pack.utils.CommonUtils.isEmpty;
import static com.gitee.starblues.plugin.pack.utils.CommonUtils.joinPath;

/**
 * 基础打包
 * @author starBlues
 * @version 3.0.0
 */

public class BasicRepackager implements Repackager{

    @Getter
    private String rootDir;
    private String relativeManifestPath;
    private String relativeResourcesDefinePath;

    private File resourcesDefineFile;

    protected final RepackageMojo repackageMojo;

    public BasicRepackager(RepackageMojo repackageMojo) {
        this.repackageMojo = repackageMojo;
    }

    @Override
    public void repackage() throws MojoExecutionException, MojoFailureException {
        rootDir = createRootDir();
        relativeManifestPath = getRelativeManifestPath();
        relativeResourcesDefinePath = getRelativeResourcesDefinePath();
        try {
            Manifest manifest = getManifest();
            writeManifest(manifest);
        } catch (Exception e) {
            repackageMojo.getLog().error(e.getMessage(), e);
            throw new MojoFailureException(e);
        }
    }

    protected String getRelativeManifestPath(){
        return MANIFEST;
    }

    protected String getRelativeResourcesDefinePath(){
        return RESOURCES_DEFINE_NAME;
    }

    protected String createRootDir() throws MojoFailureException {
        String rootDirPath = getBasicRootDir();
        File rootDir = new File(rootDirPath);
        rootDir.deleteOnExit();
        if(rootDir.mkdir()){
            return rootDirPath;
        }
        throw new MojoFailureException("创建插件根目录失败. " + rootDirPath);
    }

    protected String getBasicRootDir(){
        File outputDirectory = repackageMojo.getOutputDirectory();
        return joinPath(outputDirectory.getPath(), PackageStructure.META_INF_NAME);
    }

    protected void writeManifest(Manifest manifest) throws Exception {
        String manifestPath = CommonUtils.joinPath(rootDir, resolvePath(this.relativeManifestPath));
        File file = new File(manifestPath);
        FileOutputStream outputStream = null;
        try {
            FileUtils.forceMkdirParent(file);
            if(file.createNewFile()){
                outputStream = new FileOutputStream(file, false);
                manifest.write(outputStream);
            }
        } finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    repackageMojo.getLog().error(e.getMessage(), e);
                }
            }
        }
    }

    protected Manifest getManifest() throws Exception{
        PluginInfo pluginInfo = repackageMojo.getPluginInfo();
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.putValue("Manifest-Version", "1.0");
        attributes.putValue(PLUGIN_ID, pluginInfo.getId());
        attributes.putValue(PLUGIN_BOOTSTRAP_CLASS, pluginInfo.getBootstrapClass());
        attributes.putValue(PLUGIN_VERSION, pluginInfo.getVersion());
        attributes.putValue(PLUGIN_PATH, getPluginPath());

        String resourcesDefineFilePath = writeResourcesDefineFile();
        if(!CommonUtils.isEmpty(resourcesDefineFilePath)){
            attributes.putValue(PLUGIN_RESOURCES_CONFIG, resourcesDefineFilePath);
        }
        String configFileName = pluginInfo.getConfigFileName();
        if(!isEmpty(configFileName)){
            attributes.putValue(PLUGIN_CONFIG_FILE_NAME, configFileName);
        }
        String provider = pluginInfo.getProvider();
        if(!isEmpty(provider)){
            attributes.putValue(PLUGIN_PROVIDER, provider);
        }
        String requires = pluginInfo.getRequires();
        if(!isEmpty(requires)){
            attributes.putValue(PLUGIN_REQUIRES, requires);
        }
        String dependencyPlugins = getDependencyPlugin(pluginInfo);
        if(!isEmpty(dependencyPlugins)){
            attributes.putValue(PLUGIN_DEPENDENCIES, dependencyPlugins);
        }
        String description = pluginInfo.getDescription();
        if(!isEmpty(description)){
            attributes.putValue(PLUGIN_DESCRIPTION, description);
        }
        String license = pluginInfo.getLicense();
        if(!isEmpty(license)){
            attributes.putValue(PLUGIN_LICENSE, license);
        }
        return manifest;
    }

    protected String getDependencyPlugin(PluginInfo pluginInfo){
        List<DependencyPlugin> dependencyPlugins = pluginInfo.getDependencyPlugins();
        return AbstractDependencyPlugin.toStr(dependencyPlugins);
    }

    protected String getPluginPath(){
        DevConfig devConfig = repackageMojo.getDevConfig();
        if(devConfig != null && !isEmpty(devConfig.getPluginPath())){
            return devConfig.getPluginPath();
        }
        return repackageMojo.getProject().getBuild().getOutputDirectory();
    }

    protected String writeResourcesDefineFile() throws Exception{
        resourcesDefineFile = createResourcesDefineFile();
        writeDependenciesIndex();
        writeLoadMainResources();
        return resourcesDefineFile.getPath();
    }

    protected File createResourcesDefineFile() throws IOException {
        String path = joinPath(rootDir, resolvePath(relativeResourcesDefinePath));
        try {
            File file = new File(path);
            FileUtils.forceMkdirParent(file);
            if(file.createNewFile()){
                return file;
            }
            throw new IOException("Create " + path + " file error");
        } catch (Exception e){
            throw new IOException("Create " + path + " file error");
        }
    }

    protected void writeDependenciesIndex() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(RESOURCES_DEFINE_DEPENDENCIES).append("\n");
        Set<String> libIndex = getDependenciesIndexSet();
        for (String index : libIndex) {
            stringBuilder.append(index).append("\n");
        }
        String content = stringBuilder.toString();
        FileUtils.write(resourcesDefineFile, content, CHARSET_NAME, true);
    }

    protected Set<String> getDependenciesIndexSet() throws Exception {
        Set<Artifact> dependencies = repackageMojo.getDependencies();
        Set<String> libPaths = new HashSet<>(dependencies.size());
        for (Artifact artifact : dependencies) {
            if(filterArtifact(artifact)){
                continue;
            }
            libPaths.add(getLibIndex(artifact));
        }
        return libPaths;
    }

    protected String getLibIndex(Artifact artifact){
        return artifact.getFile().getPath();
    }

    protected void writeLoadMainResources() throws Exception {
        String loadMainResources = getLoadMainResources();
        if(CommonUtils.isEmpty(loadMainResources)){
            return;
        }
        FileUtils.write(resourcesDefineFile, loadMainResources, CHARSET_NAME, true);
    }

    protected String getLoadMainResources(){
        LoadMainResourcePattern loadMainResourcePattern = repackageMojo.getLoadMainResourcePattern();
        if(loadMainResourcePattern == null){
            return null;
        }
        String[] includes = loadMainResourcePattern.getIncludes();
        String[] excludes = loadMainResourcePattern.getExcludes();
        StringBuilder stringBuilder = new StringBuilder();
        addLoadMainResources(stringBuilder, RESOURCES_DEFINE_LOAD_MAIN_INCLUDES, includes);
        addLoadMainResources(stringBuilder, RESOURCES_DEFINE_LOAD_MAIN_EXCLUDES, excludes);
        return stringBuilder.toString();
    }

    private void addLoadMainResources(StringBuilder stringBuilder, String header, String[] patterns){
        if(CommonUtils.isEmpty(patterns)){
           return;
        }
        Set<String> patternSet = new HashSet<>(Arrays.asList(patterns));
        stringBuilder.append(header).append("\n");
        for (String patternStr : patternSet) {
            if(CommonUtils.isEmpty(patternStr)){
                continue;
            }
            stringBuilder.append(resolvePattern(patternStr)).append("\n");
        }
    }

    protected String resolvePattern(String patternStr){
        return patternStr.replace(".", "/");
    }

    /**
     * 过滤Artifact
     * @param artifact Artifact
     * @return 返回true表示被过滤掉
     */
    protected boolean filterArtifact(Artifact artifact){
        return Constant.scopeFilter(artifact.getScope());
    }


}
