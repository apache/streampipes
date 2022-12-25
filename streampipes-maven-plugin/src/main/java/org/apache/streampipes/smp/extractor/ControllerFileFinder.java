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

import org.apache.streampipes.smp.model.AssetModel;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ControllerFileFinder extends ElementFinder {

  private String filterPattern;

  public ControllerFileFinder(Log log, String baseDir, String sourceRoot, String filterPattern) {
    super(sourceRoot, log, baseDir);
    this.filterPattern = filterPattern;
  }

  public String[] findFiles() {
    FileSet fileSet = new FileSet();
    fileSet.setDirectory(sourceRoot);
    fileSet.addInclude(filterPattern);
    FileSetManager fileSetManager = new FileSetManager();

    return fileSetManager.getIncludedFiles(fileSet);
  }

  @Override
  public List<AssetModel> makeAssetModels() {
    List<AssetModel> allAssetModels = new ArrayList<>();
    for (String file : findFiles()) {
      try {
        allAssetModels.add(
            new ControllerExtractor(baseDir, sourceRoot + File.separator + file).extractControllerDetails());
      } catch (Exception e) {
        log.error(e.getMessage());
        log.info("Could not parse file " + file + ", skipping...");
      }
    }
    return allAssetModels;
  }
}
