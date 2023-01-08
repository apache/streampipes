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

package org.apache.streampipes.rest.extensions.html;

import org.apache.streampipes.rest.extensions.html.model.DataSourceDescriptionHtml;
import org.apache.streampipes.rest.extensions.html.model.Description;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;

public class JSONGenerator {

  private List<Description> description;

  public JSONGenerator(List<Description> description) {
    this.description = description;
  }

  public String buildJson() {
    JsonArray jsonArray = new JsonArray();
    description.forEach(d -> jsonArray.add(getJsonElement(d)));
    return jsonArray.toString();
  }

  private JsonObject getJsonElement(Description d) {
    JsonObject obj = makeDescription(d);
    if (d instanceof DataSourceDescriptionHtml) {
      DataSourceDescriptionHtml producerDesc = (DataSourceDescriptionHtml) d;
      JsonArray array = new JsonArray();

      producerDesc.getStreams().forEach(s -> array.add(makeDescription(s)));

      obj.add("streams", array);
    }
    return obj;
  }

  private JsonObject makeDescription(Description d) {
    JsonObject obj = new JsonObject();
    obj.add("uri", new JsonPrimitive(d.getDescriptionUrl()));
    obj.add("appId", new JsonPrimitive(d.getAppId()));
    obj.add("elementId", new JsonPrimitive(d.getElementId()));
    obj.add("name", new JsonPrimitive(d.getName()));
    obj.add("description", new JsonPrimitive(d.getDescription()));
    obj.add("type", new JsonPrimitive(d.getType()));
    obj.add("editable", new JsonPrimitive(d.isEditable()));
    obj.add("includesIcon", new JsonPrimitive(d.isIncludesIcon()));
    obj.add("includesDocs", new JsonPrimitive(d.isIncludesDocs()));
    obj.add("available", new JsonPrimitive(true));
    return obj;
  }
}
