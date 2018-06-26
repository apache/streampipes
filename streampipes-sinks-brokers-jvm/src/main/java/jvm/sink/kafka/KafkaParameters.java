/*
 * Copyright 2017 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.examples.jvm.sink.kafka;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class KafkaParameters extends EventSinkBindingParams {

  private String kafkaHost;
  private Integer kafkaPort;
  private String topic;

  public KafkaParameters(DataSinkInvocation graph, String kafkaHost, Integer kafkaPort, String topic) {
    super(graph);
    this.kafkaHost = kafkaHost;
    this.kafkaPort = kafkaPort;
    this.topic = topic;
  }

  public String getKafkaHost() {
    return kafkaHost;
  }

  public Integer getKafkaPort() {
    return kafkaPort;
  }

  public String getTopic() {
    return topic;
  }
}
