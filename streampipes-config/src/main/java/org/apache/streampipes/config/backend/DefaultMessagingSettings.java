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

package org.apache.streampipes.config.backend;

import org.apache.streampipes.model.config.MessagingSettings;
import org.apache.streampipes.model.config.SpDataFormat;
import org.apache.streampipes.model.config.SpProtocol;

import java.util.Arrays;
import java.util.List;

public class DefaultMessagingSettings {

  public static MessagingSettings make() {
    List<SpProtocol> protocolList;
    if (System.getenv(BackendConfigKeys.PRIORITIZED_PROTOCOL) != null) {
      switch (System.getenv(BackendConfigKeys.PRIORITIZED_PROTOCOL).toLowerCase()) {
        case "mqtt":
          protocolList = Arrays.asList(SpProtocol.MQTT, SpProtocol.KAFKA, SpProtocol.JMS, SpProtocol.NATS);
          break;
        case "kafka":
          protocolList = Arrays.asList(SpProtocol.KAFKA, SpProtocol.MQTT, SpProtocol.JMS, SpProtocol.NATS);
          break;
        case "jms":
          protocolList = Arrays.asList(SpProtocol.JMS, SpProtocol.KAFKA, SpProtocol.MQTT, SpProtocol.NATS);
          break;
        case "nats":
          protocolList = Arrays.asList(SpProtocol.NATS, SpProtocol.KAFKA, SpProtocol.MQTT, SpProtocol.JMS);
          break;
        default:
          protocolList = Arrays.asList(SpProtocol.KAFKA, SpProtocol.MQTT, SpProtocol.JMS, SpProtocol.NATS);
      }
    } else {
      protocolList = Arrays.asList(SpProtocol.KAFKA, SpProtocol.MQTT, SpProtocol.JMS, SpProtocol.NATS);
    }

    return new MessagingSettings(
        1638400, 5000012, 20, 2,
        Arrays.asList(SpDataFormat.JSON, SpDataFormat.CBOR, SpDataFormat.FST, SpDataFormat.SMILE),
        protocolList);
  }
}
