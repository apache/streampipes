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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ProcessingElementSerializer<T> implements JsonDeserializer<T>, JsonSerializer<T> {

  @Override
  public T deserialize(JsonElement json, Type typeOfT,
                       JsonDeserializationContext context) throws JsonParseException {

    JsonObject jsonObject = json.getAsJsonObject();
    String type = jsonObject.get("sourceClass").getAsString();

    try {
      return context.deserialize(jsonObject, Class.forName(type));
    } catch (ClassNotFoundException cnfe) {
      throw new JsonParseException("Unknown element type: " + type, cnfe);
    }
  }

  @Override
  public JsonElement serialize(T src, Type typeOfSrc,
                               JsonSerializationContext context) {
    JsonElement result = context.serialize(src, src.getClass());
    if (result.isJsonObject()) {
      JsonObject jsonObject = (JsonObject) result;
      jsonObject.add("sourceClass", new JsonPrimitive(src.getClass().getCanonicalName()));
    }
    return result;
  }
}
