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

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.model.grounding.EventGrounding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EventGroundingSerializerTest {
  @Test
  void legacyCouchGroundingWithDifferentProtocolRoundTripsWithoutBrokerFields() {
    var id = Environments.getEnvironment().getPrioritizedProtocol().getValueOrDefault();
    var type = "nats".equals(id) ? "Kafka" : "Nats";
    var json = """
        {"transportProtocols":[{"type":"org.apache.streampipes.model.grounding.%sTransportProtocol",
        "properties":{"brokerHostname":"private-host","token":"secret",
        "topicDefinition":{"type":"org.apache.streampipes.model.grounding.SimpleTopicDefinition",
        "properties":{"actualTopicName":"original.topic"}}}}]}
        """.formatted(type);
    var gson = GsonSerializer.getGson();
    var grounding = gson.fromJson(json, EventGrounding.class);
    assertEquals("original.topic", grounding.getTopicDefinition().getActualTopicName());
    var serialized = gson.toJson(grounding);
    assertFalse(serialized.contains("transportProtocols"));
    assertFalse(serialized.contains("secret"));
    assertFalse(serialized.contains("private-host"));
    assertEquals(serialized, gson.toJson(gson.fromJson(serialized, EventGrounding.class)));
  }
}
