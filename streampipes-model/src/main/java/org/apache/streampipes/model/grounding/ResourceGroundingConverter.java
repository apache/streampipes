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

package org.apache.streampipes.model.grounding;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

/** Traverses resource structure, deliberately excluding connector config and static properties. */
public final class ResourceGroundingConverter {
  private static final List<String> CHILDREN = List.of(
      "streams", "sepas", "actions", "inputStreams", "outputStream");

  private ResourceGroundingConverter() {
  }

  public static boolean convert(JsonElement resource, String protocolId) {
    if (resource == null || resource.isJsonNull()) {
      return false;
    }
    boolean changed = false;
    if (resource.isJsonArray()) {
      for (var element : resource.getAsJsonArray()) {
        changed |= convert(element, protocolId);
      }
    } else if (resource.isJsonObject()) {
      JsonObject object = resource.getAsJsonObject();
      // CouchDB polymorphic resources use a type/properties envelope.
      if ((object.has("type") || object.has("field_type")) && object.has("properties")) {
        changed |= convert(object.get("properties"), protocolId);
      }
      if (object.has("eventGrounding") && !object.get("eventGrounding").isJsonNull()) {
        changed |= LegacyGroundingConverter.convert(object.getAsJsonObject("eventGrounding"), protocolId);
      }
      for (var field : CHILDREN) {
        changed |= convert(object.get(field), protocolId);
      }
    }
    return changed;
  }
}
