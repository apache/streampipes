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
package org.streampipes.serializers.json;

import com.google.gson.*;
import org.streampipes.model.connect.adapter.AdapterDescription;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Type;

public class AdapterSerializer implements JsonSerializer<AdapterDescription>, JsonDeserializer<AdapterDescription> {


  @Override
  public AdapterDescription deserialize(JsonElement json, Type typeInfo, JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    String type = jsonObject.get("type").getAsString();
    JsonElement element = jsonObject.get("properties");

    try {
      return (AdapterDescription) GsonSerializer.getGson().fromJson(element, Class.forName(type));
    } catch (ClassNotFoundException cnfe) {
      throw new JsonParseException("Unknown element type: " + type, cnfe);
    }
  }

  @Override
  public JsonElement serialize(AdapterDescription src, Type type, JsonSerializationContext context) {
    JsonObject result = new JsonObject();
    try {
      result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
      result.add("properties", GsonSerializer.getGson().toJsonTree(src));
    } catch (MalformedParameterizedTypeException e) {
      e.printStackTrace();
    }

    return result;
  }
}
