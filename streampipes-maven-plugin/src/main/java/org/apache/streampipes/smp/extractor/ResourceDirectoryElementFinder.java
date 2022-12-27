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
package org.apache.streampipes.smp.extractor;

import org.apache.streampipes.smp.constants.PeType;
import org.apache.streampipes.smp.model.AssetModel;
import org.apache.streampipes.smp.util.Utils;

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceDirectoryElementFinder extends ElementFinder {


  public ResourceDirectoryElementFinder(String sourceRoot, Log log, String baseDir) {
    super(sourceRoot, log, baseDir);
    System.out.println(sourceRoot);
    System.out.println(baseDir);
  }

  @Override
  public List<AssetModel> makeAssetModels() {
    List<AssetModel> adapterModels = new ArrayList<>();
    Path resourceDirPath = Utils.makeResourcePath(baseDir);
    File[] resourceDirs = resourceDirPath.toFile().listFiles(File::isDirectory);
    if (resourceDirs != null) {
      List<File> appDirs = Arrays.stream(resourceDirs)
          .filter(rd -> rd.getName().startsWith("org.apache.streampipes"))
          .collect(Collectors.toList());

      appDirs.forEach(ap -> {
        try {
          adapterModels.add(extractModel(baseDir, ap));
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }

    return adapterModels;
  }

  private AssetModel extractModel(String sourceRoot, File resourceDir) throws IOException {
    AssetModel model = new AssetModel();

    model.setPeType(PeType.ADAPTER);
    model.setAppId(resourceDir.getName());
    return new LocalesExtractor(sourceRoot, model).extract();
  }

}
