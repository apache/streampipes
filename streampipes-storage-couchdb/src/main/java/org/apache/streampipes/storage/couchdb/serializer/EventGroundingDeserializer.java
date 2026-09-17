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

import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.LegacyGroundingConverter;
import org.apache.streampipes.model.grounding.TopicDefinition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public class EventGroundingDeserializer implements JsonDeserializer<EventGrounding> {

  @Override
  public EventGrounding deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
    var converted = json.getAsJsonObject().deepCopy();
    LegacyGroundingConverter.convert(converted);
    var grounding = new EventGrounding();
    grounding.setTopicDefinition(context.deserialize(converted.get("topicDefinition"), TopicDefinition.class));
    if (converted.has("options") && converted.get("options").isJsonObject()) {
      converted.getAsJsonObject("options").entrySet().forEach(entry ->
          grounding.getOptions().put(entry.getKey(), entry.getValue().getAsString()));
    }
    return grounding;
  }
}
