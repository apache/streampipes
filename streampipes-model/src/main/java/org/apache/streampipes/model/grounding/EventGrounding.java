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

import org.apache.streampipes.model.util.Cloner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonDeserialize(using = EventGroundingDeserializer.class)
public class EventGrounding {

  private TopicDefinition topicDefinition;
  private Map<String, String> options = new HashMap<>();

  public EventGrounding() {
  }

  public EventGrounding(TransportProtocol protocol) {
    setTransportProtocol(protocol);
  }

  public EventGrounding(EventGrounding other) {
    this.topicDefinition = other.topicDefinition == null ? null : new Cloner().topicDefinition(other.topicDefinition);
    this.options = new HashMap<>(other.options);
  }

  public TopicDefinition getTopicDefinition() {
    return topicDefinition;
  }

  public void setTopicDefinition(TopicDefinition topicDefinition) {
    this.topicDefinition = topicDefinition;
  }

  public Map<String, String> getOptions() {
    return options;
  }

  public void setOptions(Map<String, String> options) {
    this.options = options == null ? new HashMap<>() : new HashMap<>(options);
  }

  @JsonIgnore
  public TransportProtocol getTransportProtocol() {
    return topicDefinition == null ? null : new InternalTransportProtocol(topicDefinition, options);
  }

  @JsonIgnore
  public List<TransportProtocol> getTransportProtocols() {
    var protocol = getTransportProtocol();
    return protocol == null ? List.of() : List.of(protocol);
  }

  @JsonIgnore
  public void setTransportProtocols(List<TransportProtocol> protocols) {
    if (protocols == null || protocols.isEmpty()) {
      setTransportProtocol(null);
      return;
    }
    if (protocols.size() != 1) {
      throw new IllegalArgumentException("Ambiguous legacy event grounding");
    }
    setTransportProtocol(protocols.get(0));
  }

  @JsonIgnore
  public void setTransportProtocol(TransportProtocol protocol) {
    options = new HashMap<>();
    if (protocol == null) {
      topicDefinition = null;
      return;
    }
    topicDefinition = protocol.getTopicDefinition();
    if (protocol instanceof InternalTransportProtocol internal) {
      setOptions(internal.getOptions());
    } else if (protocol instanceof KafkaTransportProtocol kafka) {
      putOption("groupId", kafka.getGroupId());
      putOption("offset", kafka.getOffset());
      putOption("acks", kafka.getAcks());
      putOption("batchSize", kafka.getBatchSize());
      putOption("lingerMs", kafka.getLingerMs() == null ? null : kafka.getLingerMs().toString());
      putOption("messageMaxBytes", kafka.getMessageMaxBytes());
      putOption("maxRequestSize", kafka.getMaxRequestSize());
    }
  }

  private void putOption(String key, String value) {
    if (value != null) {
      options.put(key, value);
    }
  }
}
