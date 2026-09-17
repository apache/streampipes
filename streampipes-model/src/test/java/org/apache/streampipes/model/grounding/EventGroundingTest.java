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

import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventGroundingTest {
  private JsonObject legacy(String protocol) {
    return JsonParser.parseString("""
        {"transportProtocols":[{"@class":"org.apache.streampipes.model.grounding.%sTransportProtocol",
        "brokerHostname":"private-host","token":"secret","topicDefinition":{
        "@class":"org.apache.streampipes.model.grounding.SimpleTopicDefinition","actualTopicName":"original.topic"}}]}
        """.formatted(protocol)).getAsJsonObject();
  }

  @Test
  void convertsEveryProtocolAndPreservesTopic() {
    for (var protocol : List.of("Nats", "Kafka", "Mqtt", "Pulsar")) {
      assertConvertsToEveryDeploymentProtocol(protocol);
    }
  }

  private void assertConvertsToEveryDeploymentProtocol(String protocol) {
    for (var deploymentProtocol : List.of("nats", "kafka", "mqtt", "pulsar")) {
      var grounding = legacy(protocol);
      var topic = grounding.getAsJsonArray("transportProtocols").get(0).getAsJsonObject().get("topicDefinition");
      assertTrue(LegacyGroundingConverter.convert(grounding, deploymentProtocol));
      assertEquals(topic, grounding.get("topicDefinition"));
      assertFalse(grounding.toString().contains("private-host"));
      assertFalse(grounding.toString().contains("secret"));
      assertFalse(LegacyGroundingConverter.convert(grounding, deploymentProtocol));
    }
  }

  @Test
  void rejectsAmbiguousUnsupportedAndConflictingGroundings() {
    var ambiguous = legacy("Nats");
    ambiguous.getAsJsonArray("transportProtocols").add(ambiguous.getAsJsonArray("transportProtocols").get(0).deepCopy());
    assertThrows(IllegalArgumentException.class, () -> LegacyGroundingConverter.convert(ambiguous, "nats"));
    assertThrows(IllegalArgumentException.class, () -> LegacyGroundingConverter.convert(legacy("Unknown"), "nats"));
    var conflicting = legacy("Nats");
    conflicting.add("topicDefinition", new JsonObject());
    assertThrows(IllegalArgumentException.class, () -> LegacyGroundingConverter.convert(conflicting, "nats"));
  }

  @Test
  void handlesEmptyGroundingButRejectsMissingTopic() {
    var empty = JsonParser.parseString("{\"transportProtocols\":[]}").getAsJsonObject();
    assertTrue(LegacyGroundingConverter.convert(empty, "nats"));
    assertFalse(empty.has("topicDefinition"));
    var missing = legacy("Nats");
    missing.getAsJsonArray("transportProtocols").get(0).getAsJsonObject().remove("topicDefinition");
    assertThrows(IllegalArgumentException.class, () -> LegacyGroundingConverter.convert(missing, "nats"));
  }

  @Test
  void readsLegacyJacksonAndWritesOnlyLogicalGrounding() throws Exception {
    var mapper = JacksonSerializer.getObjectMapper();
    // Exercise an existing record whose protocol differs from the deployment broker.
    var protocol = org.apache.streampipes.commons.environment.Environments.getEnvironment()
        .getPrioritizedProtocol().getValueOrDefault();
    var name = "nats".equals(protocol) ? "Kafka" : "Nats";
    var grounding = mapper.readValue(legacy(name).toString(), EventGrounding.class);
    assertEquals("original.topic", grounding.getTopicDefinition().getActualTopicName());
    var serialized = mapper.writeValueAsString(grounding);
    assertFalse(serialized.contains("transportProtocol"));
    assertFalse(serialized.contains("brokerHostname"));
    assertEquals(serialized, mapper.writeValueAsString(mapper.readValue(serialized, EventGrounding.class)));
  }

  @Test
  void preservesWildcardAndKafkaOptionsInCouchEnvelope() {
    var grounding = JsonParser.parseString("""
        {"transportProtocols":[{"type":"org.apache.streampipes.model.grounding.KafkaTransportProtocol",
        "properties":{"brokerHostname":"old","kafkaPort":9092,"groupId":"group","lingerMs":3,
        "topicDefinition":{"type":"org.apache.streampipes.model.grounding.WildcardTopicDefinition",
        "properties":{"actualTopicName":"a.*","wildcardTopicName":"a.*","wildcardTopicMappings":[{"x":"y"}]}}}}]}
        """).getAsJsonObject();
    var topic = grounding.getAsJsonArray("transportProtocols").get(0).getAsJsonObject()
        .getAsJsonObject("properties").get("topicDefinition").deepCopy();
    grounding.add("topicDefinition", topic.deepCopy());
    assertTrue(LegacyGroundingConverter.convert(grounding, "nats"));
    assertEquals(topic, grounding.get("topicDefinition"));
    assertEquals("3", grounding.getAsJsonObject("options").get("lingerMs").getAsString());
    assertEquals("group", grounding.getAsJsonObject("options").get("groupId").getAsString());
  }

  @Test
  void traversesResourceLocationsButLeavesExternalConfigurationUntouched() {
    var stream = new JsonObject();
    stream.add("eventGrounding", legacy("Nats"));
    stream.addProperty("elementId", "keep-id");
    stream.addProperty("unknownField", "keep-me");
    for (var path : List.of("streams", "sepas", "actions", "inputStreams", "outputStream", "properties")) {
      var root = new JsonObject();
      root.addProperty("type", "resource");
      root.add(path, stream.deepCopy());
      root.add("config", stream.deepCopy());
      var config = root.get("config").deepCopy();
      assertTrue(ResourceGroundingConverter.convert(root, "nats"));
      assertEquals(config, root.get("config"));
      assertEquals("keep-id", root.getAsJsonObject(path).get("elementId").getAsString());
      assertEquals("keep-me", root.getAsJsonObject(path).get("unknownField").getAsString());
      assertFalse(ResourceGroundingConverter.convert(root, "nats"));
    }
  }
}
