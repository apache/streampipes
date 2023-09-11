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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.http.client.fluent.Request;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.util.List;

public class SidebarConfigGenerator {

  private static final String ExistingSidebarsUrl =
      "https://raw.githubusercontent.com/apache/streampipes-website/dev/website-v2/sidebars.json";
  private static final String DocumentationSection = "documentation";
  private static final String PipelineElementSection = "\uD83D\uDCDA Pipeline Elements";

  private final List<AssetModel> assetModels;
  private final Log log;

  public SidebarConfigGenerator(Log log,
                                List<AssetModel> assetModels) {
    this.log = log;
    this.assetModels = assetModels;
  }

  public String generate() throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    log.info("Downloading existing sidebar from Git repo");
    var existingSidebar = loadExistingSidebar();
    log.info("Sidebar download successful");
    var existingSidebarJson = JsonParser.parseString(existingSidebar);

    JsonArray pipelineElements = new JsonArray();

    pipelineElements.add(makeItems(PeType.ADAPTER, "Adapters"));
    pipelineElements.add(makeItems(PeType.PROCESSOR, "Data Processors"));
    pipelineElements.add(makeItems(PeType.SINK, "Data Sinks"));

    var section = existingSidebarJson
        .getAsJsonObject()
        .get(DocumentationSection)
        .getAsJsonObject();

    section.add(PipelineElementSection, pipelineElements);

    return gson.toJson(existingSidebarJson);
  }

  private JsonObject makeItems(PeType type,
                               String typeName) {
    var obj = new JsonObject();
    var peJsonArray = new JsonArray();
    obj.add("type", new JsonPrimitive("category"));
    obj.add("label", new JsonPrimitive(typeName));

    assetModels
        .stream()
        .filter(am -> am.getPeType() == type)
        .forEach(am -> {
          peJsonArray.add(new JsonPrimitive("pe/" + am.getAppId()));
        });

    obj.add("items", peJsonArray);
    return obj;
  }

  private String loadExistingSidebar() throws IOException {
    return Request
        .Get(ExistingSidebarsUrl)
        .execute()
        .returnContent()
        .asString();
  }
}
