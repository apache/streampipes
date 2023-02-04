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

import org.apache.streampipes.extensions.api.connect.IAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToJmsAdapterSink;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToKafkaAdapterSink;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToMqttAdapterSink;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToNatsAdapterSink;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;

import com.google.common.annotations.VisibleForTesting;

public abstract class Adapter<T extends AdapterDescription> implements IAdapter<T> {

  protected AdapterPipeline adapterPipeline;

  protected T adapterDescription;

  public Adapter(T adapterDescription) {
    this.adapterDescription = adapterDescription;
    this.adapterPipeline = getAdapterPipeline(adapterDescription);
  }

  public Adapter() { }

  @Override
  public void changeEventGrounding(TransportProtocol transportProtocol) {
    if (transportProtocol instanceof JmsTransportProtocol) {
      SendToJmsAdapterSink sink = (SendToJmsAdapterSink) this.adapterPipeline.getPipelineSink();
      sink.changeTransportProtocol((JmsTransportProtocol) transportProtocol);
    } else if (transportProtocol instanceof KafkaTransportProtocol) {
      SendToKafkaAdapterSink sink = (SendToKafkaAdapterSink) this.adapterPipeline.getPipelineSink();
      sink.changeTransportProtocol((KafkaTransportProtocol) transportProtocol);
    } else if (transportProtocol instanceof MqttTransportProtocol) {
      SendToMqttAdapterSink sink = (SendToMqttAdapterSink) this.adapterPipeline.getPipelineSink();
      sink.changeTransportProtocol((MqttTransportProtocol) transportProtocol);
    } else if (transportProtocol instanceof NatsTransportProtocol) {
      SendToNatsAdapterSink sink = (SendToNatsAdapterSink) this.adapterPipeline.getPipelineSink();
      sink.changeTransportProtocol((NatsTransportProtocol) transportProtocol);
    }
  }

  private AdapterPipeline getAdapterPipeline(T adapterDescription) {
    return new AdapterPipelineGenerator().generatePipeline(adapterDescription);
  }

  @VisibleForTesting
  public AdapterPipeline getAdapterPipeline() {
    return adapterPipeline;
  }

}
