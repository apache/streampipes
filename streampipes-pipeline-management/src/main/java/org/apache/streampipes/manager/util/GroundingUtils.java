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

package org.apache.streampipes.manager.util;

import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.InternalTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.UUID;

public class GroundingUtils {

  private static final String TOPIC_PREFIX = "org.apache.streampipes.connect.";

  public static EventGrounding createEventGrounding() {
    var grounding = new EventGrounding();
    grounding.setTopicDefinition(new SimpleTopicDefinition(TOPIC_PREFIX + UUID.randomUUID()));
    return grounding;
  }

  public static TransportProtocol makeProtocol(String topic) {
    return new InternalTransportProtocol(
        new SimpleTopicDefinition(topic), java.util.Map.of());
  }
}
