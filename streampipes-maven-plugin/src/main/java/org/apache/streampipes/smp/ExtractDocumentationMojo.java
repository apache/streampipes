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

import org.apache.streampipes.smp.extractor.ExtensionsFinder;
import org.apache.streampipes.smp.extractor.LocalesExtractor;
import org.apache.streampipes.smp.generator.AdditionalAssetsResourceGenerator;
import org.apache.streampipes.smp.generator.DocumentationResourceGenerator;
import org.apache.streampipes.smp.generator.IconResourceGenerator;
import org.apache.streampipes.smp.generator.SidebarConfigGenerator;
import org.apache.streampipes.smp.model.AssetModel;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * goal which extracts pipeline element documentations for the StreamPipes website + documentation
 */

@Mojo(
    name = "extract-docs",
    defaultPhase = LifecyclePhase.PACKAGE,
    requiresDependencyResolution = ResolutionScope.TEST,
    requiresDependencyCollection = ResolutionScope.TEST)
public class ExtractDocumentationMojo extends AbstractMojo {

  private static final String DOCS_ROOT_FOLDER = "docs";
  private static final String DOCS_PE_FOLDER = "pe";
  private static final String IMG_FOLDER = "img";
  private static final String SP_IGNORE_FILENAME = ".spignore";

  @Parameter(defaultValue = "${session}")
  private MavenSession session;

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Parameter(name = "initClass", required = true)
  private String initClass;

  @Override
  public void execute() throws MojoFailureException {
    var log = getLog();
    var targetDir = this.session.getCurrentProject().getBasedir() + File.separator + "target";
    var spIgnoreFile =
        this.session.getCurrentProject().getBasedir() + File.separator + SP_IGNORE_FILENAME;
    var docsBasePath = Paths.get(targetDir, DOCS_ROOT_FOLDER);

    try {
      List<String> pipelineElementsToExclude = new ArrayList<>();

      var path = Paths.get(spIgnoreFile);
      if (Files.exists(path)) {
        try {
          pipelineElementsToExclude = Files.readAllLines(path);
        } catch (IOException e) {
          log.error(String.format("Could not open .spignore file at path %s", path));
        }
      }

      var loader = getClassLoader(project);
      var finalPipelineElementsToExclude = pipelineElementsToExclude;
      var localesExtractor = new LocalesExtractor(log, loader);
      var extensionsElements = new ExtensionsFinder(loader, initClass)
          .findExtensions()
          .stream()
          .filter(am -> !finalPipelineElementsToExclude.contains(am.getAppId()))
          .peek(localesExtractor::applyLocales)
          .sorted(AssetModel::compareTo)
          .toList();

      log.info(String.format("Will generate documentation for %s resources", extensionsElements.size()));

      for (AssetModel extensionsElement : extensionsElements) {
        try {
          var imgPath = Paths.get(targetDir, DOCS_ROOT_FOLDER, IMG_FOLDER, extensionsElement.getAppId());
          new IconResourceGenerator(loader, extensionsElement, imgPath).generate();

          var docsPath = Paths.get(
              targetDir,
              DOCS_ROOT_FOLDER,
              DOCS_PE_FOLDER);
          new DocumentationResourceGenerator(loader, extensionsElement, docsPath).generate();

          log.info(
              String.format(
                  "Generated documentation for %s (%s, %s)",
                  extensionsElement.getAppId(),
                  extensionsElement.getPipelineElementName(),
                  extensionsElement.getPipelineElementDescription()));

          new AdditionalAssetsResourceGenerator(log, loader, extensionsElement, imgPath).generate();
        } catch (IOException e) {
          log.warn(
              String.format(
                  "Could not generate documentation for %s (%s, %s)",
                  extensionsElement.getAppId(),
                  extensionsElement.getPipelineElementName(),
                  extensionsElement.getPipelineElementDescription()), e);
        }
      }

      var sidebarConfig = new SidebarConfigGenerator(log, extensionsElements).generate();
      var sidebarPath = docsBasePath.resolve("sidebars.json");
      log.info(String.format("Writing sidebar config to %s", sidebarPath));
      FileUtils.writeStringToFile(sidebarPath.toFile(),
          sidebarConfig, Charsets.UTF_8);

    } catch (IOException | DependencyResolutionRequiredException | ClassNotFoundException
             | IllegalAccessException | InstantiationException e) {
      throw new MojoFailureException(
          "Could not generate documentation - please check the initClass for available extensions", e);
    }
  }

  private ClassLoader getClassLoader(MavenProject project)
      throws DependencyResolutionRequiredException, MalformedURLException {
    List<URL> runtimeUrls = new ArrayList<>();
    List<String> runtimeClasspathElements = project.getCompileClasspathElements();

    for (String element : runtimeClasspathElements) {
      runtimeUrls.add(new File(element).toURI().toURL());
    }

    URL[] array = new URL[runtimeUrls.size()];
    array = runtimeUrls.toArray(array);

    return new URLClassLoader(array,
        Thread.currentThread().getContextClassLoader());
  }
}
