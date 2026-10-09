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

package org.apache.streampipes.wrapper.standalone.manager;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.ProtocolOverrides;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.wrapper.standalone.routing.StandaloneSpInputCollector;
import org.apache.streampipes.wrapper.standalone.routing.StandaloneSpOutputCollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolManager {

  private static final Logger LOG = LoggerFactory.getLogger(ProtocolManager.class);
  public static Map<String, StandaloneSpInputCollector> consumers = new ConcurrentHashMap<>();
  public static Map<String, StandaloneSpOutputCollector> producers = new ConcurrentHashMap<>();

  // TODO currently only the topic name is used as an identifier for a consumer/producer. Should
  // be changed by some hashCode implementation in streampipes-model, but this requires changes
  // in empire serializers

  public static <T extends TransportProtocol> StandaloneSpInputCollector findInputCollector(T protocol,
                                                                                            Boolean singletonEngine)
      throws SpRuntimeException {
    ProtocolOverrides.addNatsTokenIfConfigured(protocol);

    var topic = topicName(protocol);
    return consumers.computeIfAbsent(topic, key -> {
      var inputCollector = makeInputCollector(protocol, singletonEngine);
      LOG.debug("Adding new consumer to consumer map (size={}): {}", consumers.size(), key);
      return inputCollector;
    });

  }

  public static <T extends TransportProtocol> StandaloneSpOutputCollector findOutputCollector(T protocol,
                                                                                              String resourceId)
      throws SpRuntimeException {
    ProtocolOverrides.addNatsTokenIfConfigured(protocol);

    var topic = topicName(protocol);
    return producers.computeIfAbsent(topic, key -> {
      var outputCollector = makeOutputCollector(protocol, resourceId);
      LOG.debug("Adding new producer to producer map (size={}): {}",
          producers.size(),
          key);
      return outputCollector;
    });

  }

  private static <T extends TransportProtocol> StandaloneSpInputCollector<T> makeInputCollector(T protocol,
                                                                                                Boolean singletonEngine)
      throws SpRuntimeException {
    return new StandaloneSpInputCollector<>(protocol, singletonEngine);
  }

  public static <T extends TransportProtocol> StandaloneSpOutputCollector<T> makeOutputCollector(T protocol,
                                                                                                 String resourceId)
      throws SpRuntimeException {
    return new StandaloneSpOutputCollector<>(protocol, resourceId);
  }

  public static void removeInputCollector(TransportProtocol protocol, StandaloneSpInputCollector<?> expected) {
    consumers.remove(topicName(protocol), expected);
  }

  public static void removeOutputCollector(TransportProtocol protocol, StandaloneSpOutputCollector<?> expected) {
    producers.remove(topicName(protocol), expected);
  }

  private static String topicName(TransportProtocol protocol) {
    return protocol.getTopicDefinition().getActualTopicName();
  }

}
