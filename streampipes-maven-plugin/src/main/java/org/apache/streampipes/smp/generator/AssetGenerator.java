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
import org.apache.streampipes.smp.parser.DocumentationParser;
import org.apache.streampipes.smp.util.Utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AssetGenerator {

  private static final String DOCUMENTATION_FILE = "documentation.md";
  private AssetModel assetModel;
  private String baseDir;

  public AssetGenerator(String baseDir, AssetModel assetModel) {
    this.assetModel = assetModel;
    this.baseDir = baseDir;
  }

  public void genreateAssetDirectoryAndContents() {
    ClassLoader classLoader = this.getClass().getClassLoader();
    String resourcePath = Utils.makePath(baseDir, assetModel.getAppId());

    Boolean dir = new File(resourcePath).mkdirs();
    InputStream inputStream = classLoader.getResourceAsStream(DOCUMENTATION_FILE);
    try {
      String content =
          new DocumentationParser(assetModel)
              .parseAndStoreDocumentation(IOUtils.toString(inputStream));
      FileUtils.writeStringToFile(new File(resourcePath + File.separator + DOCUMENTATION_FILE),
          content);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
