/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.smp;


import org.apache.streampipes.smp.extractor.ControllerFileFinder;
import org.apache.streampipes.smp.generator.AssetGenerator;
import org.apache.streampipes.smp.model.AssetModel;
import org.apache.streampipes.smp.util.DuplicateRemover;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.io.File;
import java.util.List;

/**
 * Goal which creates an asset directory.
 */
@Mojo(name = "create-assets", defaultPhase = LifecyclePhase.NONE)
public class CreateAssetMojo
    extends AbstractMojo {

  private static final String APP_ID_PROPERTY = "appId";
  private static final String GENERATE_ALL_PROPERTY = "all";
  @Parameter(defaultValue = "${session}")
  private MavenSession session;
  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;
  @Component
  private Prompter prompter;

  public void execute() throws MojoExecutionException {

    String appId = this.session.getExecutionProperties().getProperty(APP_ID_PROPERTY);
    String addAll = this.session.getExecutionProperties().getProperty(GENERATE_ALL_PROPERTY);
    File baseDir = this.project.getBasedir();

    if (appId != null) {
      getLog().info("Generating asset directory for element " + appId);
      AssetModel assetModel = new AssetModel(appId, "Pipeline Element Name", "Pipeline Element "
          + "Description");
      new AssetGenerator(baseDir.getAbsolutePath(), assetModel);
    }

    if (addAll != null) {
      List<String> sourceRoots = this.project.getCompileSourceRoots();
      getLog().info("Generating asset directories for source root " + sourceRoots.get(0));

      getLog().info("Finding controllers...");
      List<AssetModel> allAssetModels = new ControllerFileFinder(getLog(), baseDir.getAbsolutePath(),
          sourceRoots.get(0),
          "**/*Controller.java").makeAssetModels();

      getLog().info("Checking for already existing asset directories...");

      List<AssetModel> filteredModels =
          new DuplicateRemover(baseDir.getAbsolutePath(), allAssetModels).removeAlreadyExisting();

      try {
        String proceed = prompter.prompt(makeProceedText(filteredModels));
        if (proceed.equals("Y")) {
          filteredModels.forEach(am -> new AssetGenerator(baseDir.getAbsolutePath(), am)
              .genreateAssetDirectoryAndContents());
        }
      } catch (PrompterException e) {
        e.printStackTrace();
      }
    }
  }

  private String makeProceedText(List<AssetModel> assetModels) {
    StringBuilder builder = new StringBuilder();
    builder.append("The following asset directories will be created: ");

    assetModels.forEach(f -> builder.append(f.toString()));

    builder.append("Proceed? [Y][N]");
    return builder.toString();
  }

}
