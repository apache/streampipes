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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class SidebarConfigGenerator extends OutputGenerator {


  public SidebarConfigGenerator(List<AssetModel> assetModels) {
    super(assetModels);
  }

  public String generate() {
    JsonArray pipelineElements = new JsonArray();
    assetModels.forEach(pe -> pipelineElements.add("pe/" + pe.getAppId()));

    JsonObject pipelineElementObject = new JsonObject();
    pipelineElementObject.add("Pipeline Elements", pipelineElements);
    JsonObject jsonObject = new JsonObject();
    jsonObject.add("pipeline-elements", pipelineElementObject);

    return jsonObject.toString();
  }
}