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
package org.apache.streampipes.storage.couchdb.serializer;

import org.apache.streampipes.model.client.user.Principal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Type;

public class PrincipalDeserializer implements JsonDeserializer<Principal>, JsonSerializer<Principal> {
  @Override
  public Principal deserialize(JsonElement json, Type typeInfo, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    String type = jsonObject.get("field_type").getAsString();
    JsonElement element = jsonObject.get("properties");
    JsonObject tmp = element.getAsJsonObject();
    if (jsonObject.has("_id")) {
      tmp.addProperty("_id", jsonObject.get("_id").getAsString());
    }
    if (jsonObject.has("_rev")) {
      tmp.addProperty("_rev", jsonObject.get("_rev").getAsString());
    }

    try {
      return (Principal) GsonSerializer.getGson().fromJson(element, Class.forName(type));
    } catch (ClassNotFoundException cnfe) {
      throw new JsonParseException("Unknown element type: " + type, cnfe);
    }
  }

  @Override
  public JsonElement serialize(Principal src, Type type, JsonSerializationContext context) {
    JsonObject result = new JsonObject();
    try {
      // Both types are required to deserialize adapters correctly
      result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
      result.add("field_type", new JsonPrimitive(src.getClass().getCanonicalName()));
      result.add("properties", GsonSerializer.getGson().toJsonTree(src));
      if (src.getPrincipalId() != null) {
        result.add("_id", new JsonPrimitive(src.getPrincipalId()));
      }
      if (src.getRev() != null) {
        result.add("_rev", new JsonPrimitive(src.getRev()));
      }
    } catch (MalformedParameterizedTypeException e) {
      e.printStackTrace();
    }

    return result;
  }
}
