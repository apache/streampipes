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
import org.apache.streampipes.smp.extractor.ResourceDirectoryElementFinder;
import org.apache.streampipes.smp.generator.DataJsonGenerator;
import org.apache.streampipes.smp.generator.ImagePathReplacer;
import org.apache.streampipes.smp.generator.MarkdownHeaderGenerator;
import org.apache.streampipes.smp.generator.MarkdownTitleRemover;
import org.apache.streampipes.smp.generator.PipelineElementOverviewGenerator;
import org.apache.streampipes.smp.generator.SidebarConfigGenerator;
import org.apache.streampipes.smp.model.AssetModel;
import org.apache.streampipes.smp.util.DirectoryManager;
import org.apache.streampipes.smp.util.Utils;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * goal which extracts pipeline element documentations for the StreamPipes website + documentation
 */

@Mojo(name = "extract-docs", defaultPhase = LifecyclePhase.NONE, aggregator = true)
public class ExtractDocumentationMojo extends AbstractMojo {

  private static final String DOCS_ROOT_FOLDER = "docs";
  private static final String DOCS_FOLDER = "docs-documentation";
  private static final String DOCS_WEBSITE_FOLDER = "docs-website";
  private static final String IMG_FOLDER = "img";
  private static final String SP_IGNORE_FILENAME = ".spignore";

  @Parameter(defaultValue = "${session}")
  private MavenSession session;

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    String targetDir = this.session.getExecutionRootDirectory() + File.separator + "target";
    String spIgnoreFile =
        this.session.getExecutionRootDirectory() + File.separator + SP_IGNORE_FILENAME;
    Path docsBasePath = Paths.get(targetDir, DOCS_ROOT_FOLDER);

    List<MavenProject> projects = session.getProjects();
    List<AssetModel> documentedPipelineElements = new ArrayList<>();
    List<String> pipelineElementsToExclude = new ArrayList<>();

    if (Files.exists(Paths.get(spIgnoreFile))) {
      try {
        pipelineElementsToExclude = Files.readAllLines(Paths.get(spIgnoreFile));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    for (MavenProject currentModule : projects) {
      File baseDir = currentModule.getBasedir();
      List<String> sourceRoots = currentModule.getCompileSourceRoots();
      List<AssetModel> allAssetModels = new ArrayList<>();
      //String targetDir = this.project.getModel().getBuild().getDirectory();

      //String dockerImageName =
      //        new DockerImageExtractor(baseDir.getAbsolutePath()).extractImageName();

      if (currentModule.getName().equals("streampipes-connect-adapters")) {
        System.out.println("Opening adapters");
        allAssetModels = new ResourceDirectoryElementFinder(sourceRoots.get(0), getLog(), baseDir.getAbsolutePath())
            .makeAssetModels();
        allAssetModels.forEach(am -> System.out.println(am.getAppId()));
      } else {
        allAssetModels = new ControllerFileFinder(getLog(),
            baseDir.getAbsolutePath(), sourceRoots.get(0),
            "**/*Controller.java").makeAssetModels();
      }

      allAssetModels.forEach(am -> {
        am.setBaseDir(baseDir.getAbsolutePath());
        am.setModuleName(currentModule.getName());
        //am.setContainerName(dockerImageName);
      });

      List<String> finalPipelineElementsToExclude = pipelineElementsToExclude;
      documentedPipelineElements.addAll(
          allAssetModels
              .stream()
              .filter(am -> finalPipelineElementsToExclude.stream().noneMatch(pe -> pe.equals(am.getAppId())))
              .collect(Collectors.toList()));
    }
    try {
      Collections.sort(documentedPipelineElements);
      documentedPipelineElements.forEach(am -> System.out.println(
          am.getAppId() + ", " + am.getPipelineElementName() + ", " + am.getPipelineElementDescription()));

      for (AssetModel assetModel : documentedPipelineElements) {
        Path docsPath = Paths.get(targetDir, DOCS_ROOT_FOLDER, DOCS_FOLDER,
            "pe");
        Path docsWebsitePath = Paths.get(targetDir, DOCS_ROOT_FOLDER, DOCS_WEBSITE_FOLDER,
            assetModel.getAppId());
        Path imgPath = Paths.get(targetDir, DOCS_ROOT_FOLDER, IMG_FOLDER,
            assetModel.getAppId());
        DirectoryManager.createIfNotExists(docsPath);
        DirectoryManager.createIfNotExists(imgPath);

        Boolean iconExists = Files.exists(Utils.makeResourcePath(assetModel.getBaseDir(),
            assetModel.getAppId()).resolve("icon.png"));

        if (iconExists) {
          Files.copy(Utils.makeResourcePath(assetModel.getBaseDir(),
                  assetModel.getAppId()).resolve("icon.png")
              , imgPath.resolve("icon.png"));
        } else {
          ClassLoader classLoader = this.getClass().getClassLoader();
          InputStream inputStream = classLoader.getResourceAsStream("placeholder-icon.png");
          Files.copy(inputStream, imgPath.resolve("icon.png"));
        }

        String originalDocumentationFileContents =
            FileUtils.readFileToString(Utils.makeResourcePath(assetModel.getBaseDir(),
                assetModel.getAppId()).resolve("documentation.md").toFile());

        // modify docs for documentation page
        String documentationFileContents =
            new MarkdownTitleRemover(originalDocumentationFileContents).removeTitle();

        documentationFileContents =
            new MarkdownHeaderGenerator(assetModel, documentationFileContents).createHeaders();

        documentationFileContents = new ImagePathReplacer(documentationFileContents,
            assetModel.getAppId()).replaceContentForDocs();

        FileUtils.writeStringToFile(docsPath.resolve(assetModel.getAppId() + ".md").toFile(),
            documentationFileContents);

      }

      Boolean existsOverviewFile = Files.exists(docsBasePath.resolve("pipeline-elements.md"));
      if (!existsOverviewFile) {
        AssetModel assetModel = new AssetModel("pipeline-elements", "Overview", "");
        String header = new MarkdownHeaderGenerator(assetModel, "").createHeaders();
        FileUtils.writeStringToFile(docsBasePath.resolve("pipeline-elements.md").toFile(), header);
      }

      String pipelineElementOverviewContent =
          new PipelineElementOverviewGenerator(documentedPipelineElements).generate();
      FileUtils.writeStringToFile(docsBasePath.resolve("pipeline-elements.md").toFile(),
          pipelineElementOverviewContent, true);


      FileUtils.writeStringToFile(docsBasePath.resolve("sidebars.json").toFile(),
          new SidebarConfigGenerator(documentedPipelineElements).generate());

      FileUtils.writeStringToFile(docsBasePath.resolve("_data.json").toFile(),
          new DataJsonGenerator(documentedPipelineElements).generate());


    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
