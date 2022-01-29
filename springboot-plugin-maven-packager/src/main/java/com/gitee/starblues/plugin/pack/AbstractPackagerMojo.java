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

import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.plugin.pack.utils.CommonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Set;

/**
 * @author starBlues
 * @version 3.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractPackagerMojo extends AbstractDependencyFilterMojo{

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File outputDirectory;

    @Parameter(property = "springboot-plugin.mode", defaultValue = "dev", required = true)
    private String mode;

    @Parameter(property = "springboot-plugin.skip", defaultValue = "false")
    private boolean skip;

    @Parameter(property = "springboot-plugin.pluginInfo", required = true)
    private PluginInfo pluginInfo;

    @Parameter(property = "springboot-plugin.loadMainResourcePattern", required = false)
    private LoadMainResourcePattern loadMainResourcePattern;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        if(Constant.isPom(this.getProject().getPackaging())){
            getLog().debug("repackage goal could not be applied to pom project.");
            return;
        }
        if (this.skip) {
            getLog().debug("skipping plugin package.");
            return;
        }
        checkPluginInfo();
        pack();
    }

    protected abstract void pack() throws MojoExecutionException, MojoFailureException;

    public final Set<Artifact> getDependencies() throws MojoExecutionException {
        return filterDependencies(project.getArtifacts(), getFilters());
    }

    private void checkPluginInfo() throws MojoExecutionException {
        if(pluginInfo == null){
            throw new MojoExecutionException("configuration.pluginInfo config cannot be empty");
        }
        if(CommonUtils.isEmpty(pluginInfo.getId())){
            throw new MojoExecutionException("configuration.pluginInfo.id config cannot be empty");
        } else {
            String id = pluginInfo.getId();
            String illegal = PackageStructure.getIllegal(id);
            if(illegal != null){
                throw new MojoExecutionException("configuration.pluginInfo.id config can't contain: " + illegal);
            }
        }
        if(CommonUtils.isEmpty(pluginInfo.getBootstrapClass())){
            throw new MojoExecutionException("configuration.pluginInfo.bootstrapClass config cannot be empty");
        }
        if(CommonUtils.isEmpty(pluginInfo.getVersion())){
            throw new MojoExecutionException("configuration.pluginInfo.version config cannot be empty");
        } else {
            String version = pluginInfo.getVersion();
            String illegal = PackageStructure.getIllegal(version);
            if(illegal != null){
                throw new MojoExecutionException("configuration.pluginInfo.version config can't contain: " + illegal);
            }
        }
    }
}
