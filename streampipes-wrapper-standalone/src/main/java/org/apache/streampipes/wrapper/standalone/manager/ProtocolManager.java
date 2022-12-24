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
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.wrapper.standalone.routing.StandaloneSpInputCollector;
import org.apache.streampipes.wrapper.standalone.routing.StandaloneSpOutputCollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ProtocolManager {

  private static final Logger LOG = LoggerFactory.getLogger(ProtocolManager.class);
  public static Map<String, StandaloneSpInputCollector> consumers = new HashMap<>();
  public static Map<String, StandaloneSpOutputCollector> producers = new HashMap<>();

  // TODO currently only the topic name is used as an identifier for a consumer/producer. Should
  // be changed by some hashCode implementation in streampipes-model, but this requires changes
  // in empire serializers

  public static <T extends TransportProtocol> StandaloneSpInputCollector findInputCollector(T protocol,
                                                                                            TransportFormat format,
                                                                                            Boolean singletonEngine)
      throws SpRuntimeException {

    if (consumers.containsKey(topicName(protocol))) {
      return consumers.get(topicName(protocol));
    } else {
      consumers.put(topicName(protocol), makeInputCollector(protocol, format, singletonEngine));
      LOG.info("Adding new consumer to consumer map (size=" + consumers.size() + "): " + topicName(protocol));
      return consumers.get(topicName(protocol));
    }

  }

  public static <T extends TransportProtocol> StandaloneSpOutputCollector findOutputCollector(T protocol,
                                                                                              TransportFormat format,
                                                                                              String resourceId)
      throws SpRuntimeException {

    if (producers.containsKey(topicName(protocol))) {
      return producers.get(topicName(protocol));
    } else {
      producers.put(topicName(protocol), makeOutputCollector(protocol, format, resourceId));
      LOG.info("Adding new producer to producer map (size=" + producers.size() + "): " + topicName
          (protocol));
      return producers.get(topicName(protocol));
    }

  }

  private static <T extends TransportProtocol> StandaloneSpInputCollector<T> makeInputCollector(T protocol,
                                                                                                TransportFormat format,
                                                                                                Boolean singletonEngine)
      throws SpRuntimeException {
    return new StandaloneSpInputCollector<>(protocol, format, singletonEngine);
  }

  public static <T extends TransportProtocol> StandaloneSpOutputCollector<T> makeOutputCollector(T protocol,
                                                                                                 TransportFormat format,
                                                                                                 String resourceId)
      throws SpRuntimeException {
    return new StandaloneSpOutputCollector<>(protocol, format, resourceId);
  }

  private static String topicName(TransportProtocol protocol) {
    return protocol.getTopicDefinition().getActualTopicName();
  }

  public static <T extends TransportProtocol> void removeInputCollector(T protocol) throws
      SpRuntimeException {
    consumers.remove(topicName(protocol));
    LOG.info("Removing consumer from consumer map (size=" + consumers.size() + "): " + topicName
        (protocol));
  }

  public static <T extends TransportProtocol> void removeOutputCollector(T protocol) throws
      SpRuntimeException {
    producers.remove(topicName(protocol));
    LOG.info("Removing producer from producer map (size=" + producers.size() + "): " + topicName
        (protocol));
  }


}
