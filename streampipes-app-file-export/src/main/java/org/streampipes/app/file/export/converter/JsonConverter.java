/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.app.file.export.converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class JsonConverter {

  private JsonObject elasticJsonRepresentation;

  public JsonConverter(String elasticJsonRepresentation) {
    this.elasticJsonRepresentation = new JsonParser().parse(elasticJsonRepresentation).getAsJsonObject();
  }

  public String convertToJson() {
    return extractContent().toString();
  }

  public String convertToCsv() {
    JsonArray inContent = extractContent();
    StringBuilder sb = new StringBuilder();

    for(int i = 0; i < inContent.size(); i++) {
      JsonObject jsonObject = inContent.get(i).getAsJsonObject();
      Set<Map.Entry<String, JsonElement>> elements = jsonObject.entrySet();
      StringJoiner sj = new StringJoiner(";");
      for (Map.Entry<String, JsonElement> entry: elements) {
        sj.add(entry.getValue().toString());
      }
      sb.append(sj.toString());
      sb.append("\n");
    }

    return sb.toString();
  }

  private JsonArray extractContent() {
    JsonArray inContent = elasticJsonRepresentation.get("hits").getAsJsonObject().get("hits").getAsJsonArray();
    JsonArray outContent = new JsonArray();

    for(int i = 0; i < inContent.size(); i++) {
      JsonObject jsonObject = inContent.get(i).getAsJsonObject().get("_source").getAsJsonObject();
      outContent.add(jsonObject);
    }

    return outContent;
  }
}
