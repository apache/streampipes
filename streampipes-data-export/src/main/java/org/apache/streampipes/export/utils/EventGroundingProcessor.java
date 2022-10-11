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

package org.apache.streampipes.export.utils;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;

public class EventGroundingProcessor {

  public static void applyOverride(TransportProtocol protocol) {
    if (protocol instanceof KafkaTransportProtocol) {
      protocol.setBrokerHostname(BackendConfig.INSTANCE.getKafkaHost());
      ((KafkaTransportProtocol) protocol).setKafkaPort(BackendConfig.INSTANCE.getKafkaPort());
    } else if (protocol instanceof MqttTransportProtocol) {
      protocol.setBrokerHostname(BackendConfig.INSTANCE.getMqttHost());
      ((MqttTransportProtocol) protocol).setPort(BackendConfig.INSTANCE.getMqttPort());
    }
  }
}
