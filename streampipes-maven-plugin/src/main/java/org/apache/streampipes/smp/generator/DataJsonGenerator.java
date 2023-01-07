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

import org.apache.streampipes.smp.constants.PeType;
import org.apache.streampipes.smp.model.AssetModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class DataJsonGenerator extends OutputGenerator {

  public DataJsonGenerator(List<AssetModel> assetModels) {
    super(assetModels);
  }

  @Override
  public String generate() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.add("pipelineElements", makePipelineElementList());

    return jsonObject.toString();
  }

  private JsonArray makePipelineElementList() {
    JsonArray elementList = new JsonArray();

    assetModels.forEach(am -> {
      elementList.add(makePipelineElementEntry(am));
    });

    return elementList;
  }

  private JsonObject makePipelineElementEntry(AssetModel am) {
    JsonObject entry = new JsonObject();
    entry.addProperty("appId", am.getAppId());
    entry.addProperty("type", am.getPeType().getFriendly());
    entry.addProperty("name", am.getPipelineElementName());
    entry.addProperty("description", am.getPipelineElementDescription());
    entry.addProperty("dockerHubLink", makeDockerHubLink(am.getPeType(), am.getModuleName()));
    entry.addProperty("githubLink", makeGithubLink(am.getModuleName()));

    return entry;
  }

  private String makeDockerHubLink(PeType peType, String moduleName) {
    String dockerHubRepoName = peType == PeType.ADAPTER ? "connect-worker" : moduleName.replace("streampipes-", "");
    return "https://hub.docker.com/r/apachestreampipes/" + dockerHubRepoName;
  }

  private String makeGithubLink(String moduleName) {
    return "https://github.com/apache/incubator-streampipes-extensions/tree/dev/" + moduleName;
  }


}
