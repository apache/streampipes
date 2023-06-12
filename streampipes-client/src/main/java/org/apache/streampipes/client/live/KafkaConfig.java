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
package org.apache.streampipes.client.live;

import org.apache.streampipes.client.api.live.IKafkaConfig;

public class KafkaConfig implements IKafkaConfig {

  private String kafkaHost;
  private Integer kafkaPort;

  private KafkaConfig(String kafkaHost, Integer kafkaPort) {
    this.kafkaHost = kafkaHost;
    this.kafkaPort = kafkaPort;
  }

  public static KafkaConfig create(String kafkaHost, Integer kafkaPort) {
    return new KafkaConfig(kafkaHost, kafkaPort);
  }

  @Override
  public String getKafkaHost() {
    return kafkaHost;
  }

  @Override
  public Integer getKafkaPort() {
    return kafkaPort;
  }
}
