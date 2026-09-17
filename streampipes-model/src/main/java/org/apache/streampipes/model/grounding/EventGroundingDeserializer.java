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

import org.apache.streampipes.commons.environment.Environments;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class EventGroundingDeserializer extends JsonDeserializer<EventGrounding> {

  @Override
  public EventGrounding deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    JsonNode node = parser.getCodec().readTree(parser);
    if (node.has("transportProtocols")) {
      var json = com.google.gson.JsonParser.parseString(node.toString()).getAsJsonObject();
      LegacyGroundingConverter.convert(json, Environments.getEnvironment().getPrioritizedProtocol().getValueOrDefault());
      try (var convertedParser = parser.getCodec().getFactory().createParser(json.toString())) {
        node = parser.getCodec().readTree(convertedParser);
      }
    }
    var result = new EventGrounding();
    var topic = node.get("topicDefinition");
    if (topic != null && !topic.isNull()) {
      result.setTopicDefinition(parser.getCodec().treeToValue(topic, TopicDefinition.class));
    }
    var options = node.get("options");
    if (options != null && options.isObject()) {
      options.fields().forEachRemaining(entry -> result.getOptions().put(entry.getKey(), entry.getValue().asText()));
    }
    return result;
  }
}
