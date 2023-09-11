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

import org.apache.streampipes.smp.model.AssetModel;
import org.apache.streampipes.smp.util.DirectoryManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class IconResourceGenerator extends ResourceGenerator {

  public static final String ICON_NAME = "icon.png";
  private static final String PlaceholderIconName = "placeholder-icon.png";

  public IconResourceGenerator(ClassLoader loader,
                               AssetModel extensionsElement,
                               Path targetPath) {
    super(loader, extensionsElement, targetPath);
  }

  @Override
  public void generate() throws IOException {
    DirectoryManager.createIfNotExists(targetPath);
    try (var inputStream = getResourceInputStream(ICON_NAME)) {
      if (inputStream != null) {
        Files.copy(inputStream, targetPath.resolve(ICON_NAME), StandardCopyOption.REPLACE_EXISTING);
      } else {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (var placeHolderIconStream = classLoader.getResourceAsStream(PlaceholderIconName)) {
          if (placeHolderIconStream != null) {
            Files.copy(placeHolderIconStream, targetPath.resolve(ICON_NAME), StandardCopyOption.REPLACE_EXISTING);
          }
        }
      }
    }
  }
}
