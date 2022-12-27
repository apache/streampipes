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

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ControllerExtractor {

  private static String declareModelMethod = "declareModel";
  private String filename;
  private String baseDir;

  public ControllerExtractor(String baseDir, String filename) {
    this.filename = filename;
    this.baseDir = baseDir;
  }

  public AssetModel extractControllerDetails() throws Exception {
    String fileContents = new String(Files.readAllBytes(Paths.get(filename)));
    JavaClassSource clazz = Roaster.parse(JavaClassSource.class, fileContents);
    if (clazz.hasMethodSignature(declareModelMethod)) {
      String declareMethodContent = (clazz.getMethod(declareModelMethod).getBody());
      declareMethodContent =
          new FieldReplacer(clazz, declareMethodContent).replaceDeclareModelContent();
      AssetModel assetModel = new AssetModelItemExtractor(declareMethodContent).extractAssetItem();
      if (assetModel.getPipelineElementName() == null) {
        assetModel = new LocalesExtractor(baseDir, assetModel).extract();
      }
      return assetModel;

    } else {
      throw new Exception("Not a valid controller class or could not read id elements");
    }
  }
}
