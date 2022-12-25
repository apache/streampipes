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

package org.apache.streampipes.smp.util;

import org.apache.streampipes.smp.model.AssetModel;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class DuplicateRemover {

  private List<AssetModel> assetModels;
  private String sourceDir;

  public DuplicateRemover(String sourceDir, List<AssetModel> assetModels) {
    this.assetModels = assetModels;
    this.sourceDir = sourceDir;
  }

  public List<AssetModel> removeAlreadyExisting() {

    return assetModels
        .stream()
        .filter(am -> !(new File(Utils.makePath(sourceDir, am.getAppId() + File.separator
            + "documentation.md")).exists()))
        .collect(Collectors.toList());
  }
}
