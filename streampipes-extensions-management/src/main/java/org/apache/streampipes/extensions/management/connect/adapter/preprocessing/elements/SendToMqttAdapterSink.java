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
package org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements;

import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.mqtt.MqttPublisher;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;

public class SendToMqttAdapterSink extends SendToBrokerAdapterSink<MqttTransportProtocol>
    implements IAdapterPipelineElement {

  public SendToMqttAdapterSink(AdapterDescription adapterDescription) {
    super(adapterDescription, MqttTransportProtocol.class);
  }

  @Override
  protected EventProducer makeProducer(MqttTransportProtocol protocol) {
    return new MqttPublisher(protocol);
  }

  @Override
  public void modifyProtocolForDebugging(MqttTransportProtocol transportProtocol) {
    protocol.setBrokerHostname("localhost");
  }
}
