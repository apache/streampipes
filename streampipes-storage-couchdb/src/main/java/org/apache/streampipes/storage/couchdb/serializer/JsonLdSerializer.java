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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Type;

public class JsonLdSerializer<T> implements JsonDeserializer<T>, JsonSerializer<T> {

  private static final Logger LOG = LoggerFactory.getLogger(JsonLdSerializer.class);

  @Override
  public T deserialize(JsonElement json, Type typeOfT,
                       JsonDeserializationContext context) throws JsonParseException {

    JsonObject jsonObject = json.getAsJsonObject();
    String type = jsonObject.get("type").getAsString();
    JsonElement element = jsonObject.get("properties");

    try {
      return context.deserialize(element, Class.forName(type));
    } catch (ClassNotFoundException cnfe) {
      throw new JsonParseException("Unknown element type: " + type, cnfe);
    }
  }

  @Override
  public JsonElement serialize(T src, Type typeOfSrc,
                               JsonSerializationContext context) {
    JsonObject result = new JsonObject();
    try {
      result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
      result.add("properties", context.serialize(src, src.getClass()));
    } catch (MalformedParameterizedTypeException e) {
      LOG.error("Could not serialize class {}", src.getClass().getCanonicalName(), e);
    }

    return result;
  }

}
