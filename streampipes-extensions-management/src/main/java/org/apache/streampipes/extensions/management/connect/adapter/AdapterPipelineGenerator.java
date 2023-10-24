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

package org.apache.streampipes.extensions.management.connect.adapter;

import org.apache.streampipes.connect.shared.AdapterPipelineGeneratorBase;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.extensions.management.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToBrokerAdapterSink;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToJmsAdapterSink;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToKafkaAdapterSink;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToMqttAdapterSink;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToNatsAdapterSink;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToPulsarAdapterSink;
import org.apache.streampipes.model.configuration.MessagingSettings;
import org.apache.streampipes.model.configuration.SpProtocol;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.PulsarTransportProtocol;

public class AdapterPipelineGenerator extends AdapterPipelineGeneratorBase {

  public AdapterPipeline generatePipeline(AdapterDescription adapterDescription) {

    var pipelineElements = makeAdapterPipelineElements(adapterDescription.getRules(), true);

    if (hasValidGrounding(adapterDescription)) {
      return new AdapterPipeline(
          pipelineElements,
          getAdapterSink(adapterDescription),
          adapterDescription.getEventSchema());
    } else {
      return new AdapterPipeline(pipelineElements, adapterDescription.getEventSchema());
    }
  }

  private SendToBrokerAdapterSink<?> getAdapterSink(AdapterDescription adapterDescription) {
    var prioritizedProtocol =
        getMessagingSettings().getPrioritizedProtocols().get(0);

    if (isPrioritized(prioritizedProtocol, JmsTransportProtocol.class)) {
      return new SendToJmsAdapterSink(adapterDescription);
    } else if (isPrioritized(prioritizedProtocol, KafkaTransportProtocol.class)) {
      return new SendToKafkaAdapterSink(adapterDescription);
    } else if (isPrioritized(prioritizedProtocol, MqttTransportProtocol.class)) {
      return new SendToMqttAdapterSink(adapterDescription);
    } else if (isPrioritized(prioritizedProtocol, PulsarTransportProtocol.class)) {
      return new SendToPulsarAdapterSink(adapterDescription);
    } else {
      return new SendToNatsAdapterSink(adapterDescription);
    }
  }

  private boolean isPrioritized(SpProtocol prioritizedProtocol,
                                Class<?> protocolClass) {
    return prioritizedProtocol.getProtocolClass().equals(protocolClass.getCanonicalName());
  }

  private MessagingSettings getMessagingSettings() {
    var client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    return client.adminApi().getMessagingSettings();
  }

  private boolean hasValidGrounding(AdapterDescription adapterDescription) {
    return adapterDescription.getEventGrounding() != null
        && adapterDescription.getEventGrounding().getTransportProtocol() != null
        && adapterDescription.getEventGrounding().getTransportProtocol().getBrokerHostname() != null;
  }
}
