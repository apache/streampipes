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
import java.util.Map;

/** Converts a grounding without rewriting its enclosing resource or topic definition. */
public final class LegacyGroundingConverter {

  private static final Map<String, String> PROTOCOLS = Map.of(
      "KafkaTransportProtocol", "kafka", "NatsTransportProtocol", "nats",
      "MqttTransportProtocol", "mqtt", "PulsarTransportProtocol", "pulsar");
  private static final List<String> OPTIONS = List.of(
      "groupId", "offset", "acks", "batchSize", "lingerMs", "messageMaxBytes", "maxRequestSize");

  private LegacyGroundingConverter() {
  }

  public static boolean convert(JsonObject grounding, String configuredProtocol) {
    if (!grounding.has("transportProtocols")) {
      return false;
    }
    var legacy = grounding.get("transportProtocols");
    if (legacy.isJsonNull() || (legacy.isJsonArray() && legacy.getAsJsonArray().isEmpty())) {
      grounding.remove("transportProtocols");
      return true;
    }
    if (!legacy.isJsonArray() || legacy.getAsJsonArray().size() != 1) {
      throw new IllegalArgumentException("Ambiguous legacy event grounding");
    }
    var protocol = legacy.getAsJsonArray().get(0).getAsJsonObject();
    var type = protocol.has("type") ? protocol.get("type") : protocol.get("@class");
    if (type == null || type.isJsonNull()) {
      throw new IllegalArgumentException("Missing legacy protocol type");
    }
    String className = type.getAsString();
    String id = PROTOCOLS.get(className.substring(className.lastIndexOf('.') + 1));
    if (id == null) {
      throw new IllegalArgumentException("Unsupported legacy protocol type");
    }
    // Discard the legacy connection even when its protocol differs from the deployment broker.
    // The resulting logical grounding is bound to the configured broker by the runtime.
    var fields = protocol.has("properties") ? protocol.getAsJsonObject("properties") : protocol;
    var topic = fields.get("topicDefinition");
    validateTopic(topic);
    if (grounding.has("topicDefinition") && !grounding.get("topicDefinition").equals(topic)) {
      throw new IllegalArgumentException("Conflicting legacy and current topic definitions");
    }
    var options = grounding.has("options") && !grounding.get("options").isJsonNull()
        ? grounding.getAsJsonObject("options").deepCopy() : new JsonObject();
    if ("kafka".equals(id)) {
      for (String key : OPTIONS) {
        if (fields.has(key) && !fields.get(key).isJsonNull()) {
          String value = fields.get(key).getAsString();
          if (options.has(key) && !options.get(key).getAsString().equals(value)) {
            throw new IllegalArgumentException("Conflicting legacy channel option");
          }
          options.addProperty(key, value);
        }
      }
    }
    grounding.add("topicDefinition", topic.deepCopy());
    grounding.add("options", options);
    grounding.remove("transportProtocols");
    return true;
  }

  private static void validateTopic(JsonElement topic) {
    if (topic == null || !topic.isJsonObject()) {
      throw new IllegalArgumentException("Missing legacy topic definition");
    }
    var object = topic.getAsJsonObject();
    var type = object.has("type") ? object.get("type") : object.get("@class");
    if (type == null || type.isJsonNull()
        || !List.of(SimpleTopicDefinition.class.getCanonicalName(), WildcardTopicDefinition.class.getCanonicalName())
            .contains(type.getAsString())) {
      throw new IllegalArgumentException("Unsupported legacy topic definition type");
    }
    var fields = object.has("properties") ? object.getAsJsonObject("properties") : object;
    var name = fields.get("actualTopicName");
    var wildcard = fields.get("wildcardTopicName");
    if ((name == null || name.isJsonNull() || name.getAsString().isBlank())
        && (wildcard == null || wildcard.isJsonNull() || wildcard.getAsString().isBlank())) {
      throw new IllegalArgumentException("Missing legacy topic name");
    }
  }
}
