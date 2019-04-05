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
  private JsonParser jsonParser;

  public JsonConverter() {
    this.jsonParser = new JsonParser();
  }

  public String getCsvHeader(String elasticJsonRepresentation) {
    JsonObject inContent = jsonParser.parse(elasticJsonRepresentation).getAsJsonObject();

    Set<Map.Entry<String, JsonElement>> elements = inContent.entrySet();
    StringJoiner sj = new StringJoiner(";");

    for (Map.Entry<String, JsonElement> entry: elements) {
      sj.add(entry.getKey().toString());
    }

    return sj.toString() + "\n";

  }

  public String convertToCsv(String elasticJsonRepresentation) {
    JsonObject inContent = jsonParser.parse(elasticJsonRepresentation).getAsJsonObject();

    Set<Map.Entry<String, JsonElement>> elements = inContent.entrySet();
    StringJoiner sj = new StringJoiner(";");

    for (Map.Entry<String, JsonElement> entry: elements) {
      sj.add(entry.getValue().toString());
    }

    return sj.toString() + "\n";

  }

}
