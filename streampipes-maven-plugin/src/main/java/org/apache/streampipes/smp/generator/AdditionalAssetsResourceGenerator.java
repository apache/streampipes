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

package org.apache.streampipes.smp.generator;

import org.apache.streampipes.smp.extractor.LocalesExtractor;
import org.apache.streampipes.smp.model.AssetModel;

import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AdditionalAssetsResourceGenerator extends ResourceGenerator {

  private static final String Slash = "/";

  private final Log log;

  public AdditionalAssetsResourceGenerator(Log log,
                                           ClassLoader loader,
                                           AssetModel extensionsElement,
                                           Path targetPath) {
    super(loader, extensionsElement, targetPath);
    this.log = log;
  }

  @Override
  public void generate() throws IOException {
    var resourceName = extensionsElement.getAppId() + Slash;
    var resources = loader.getResources(resourceName);
    while (resources.hasMoreElements()) {
      URL resourceUrl = resources.nextElement();
      if (resourceUrl.getProtocol().equals("jar")) {
        JarURLConnection jarConnection = (JarURLConnection) resourceUrl.openConnection();
        try (JarFile jarFile = jarConnection.getJarFile()) {
          Enumeration<JarEntry> entries = jarFile.entries();
          while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.isDirectory() && entry.getName().startsWith(resourceName)) {
              var filename = entry.getName();
              if (!isDefaultResource(filename)) {
                try (InputStream inputStream = jarFile.getInputStream(entry)) {
                  Files.createDirectories(targetPath);
                  filename = filename.substring(filename.lastIndexOf(Slash) + 1);
                  Files.copy(inputStream, targetPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
                  log.info(String.format("Additional resource file %s copied to %s", filename, targetPath));
                }
              }
            }
          }
        }
      }
    }
  }

  private boolean isDefaultResource(String resourceName) {
    var defaultResources = List.of(
        IconResourceGenerator.ICON_NAME,
        DocumentationResourceGenerator.DOCUMENTATION_FILE_NAME,
        LocalesExtractor.LOCALES_FILE_EN);
    return defaultResources.stream().anyMatch(resourceName::contains);
  }
}
