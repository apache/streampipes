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

import org.apache.streampipes.model.schema.EventProperty;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class EventPropertySerializer extends CouchDbJsonSerializer<EventProperty> {

  private static final String DomainProperties = "domainProperties";

  @Override
  public EventProperty deserialize(JsonElement json, Type typeOfT,
                       JsonDeserializationContext context) throws JsonParseException {

    JsonObject jsonObject = json.getAsJsonObject();
    String type = jsonObject.get("type").getAsString();
    JsonElement element = jsonObject.get("properties");
    if (element.getAsJsonObject().has(DomainProperties)) {
      var domainProperties = element.getAsJsonObject().get(DomainProperties).getAsJsonArray();
      if (!domainProperties.isEmpty()) {
        element.getAsJsonObject().add("semanticType", new JsonPrimitive(domainProperties.get(0).getAsString()));
      }
      element.getAsJsonObject().remove(DomainProperties);

    }

    try {
      return context.deserialize(element, Class.forName(type));
    } catch (ClassNotFoundException cnfe) {
      throw new JsonParseException("Unknown element type: " + type, cnfe);
    }
  }
}
