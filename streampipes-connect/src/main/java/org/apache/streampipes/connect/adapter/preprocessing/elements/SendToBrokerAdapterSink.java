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
package org.apache.streampipes.connect.adapter.preprocessing.elements;

import org.apache.streampipes.commons.constants.Envs;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.connect.api.IAdapterPipelineElement;
import org.apache.streampipes.connect.adapter.util.TransportFormatSelector;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.Map;
import java.util.function.Supplier;

public abstract class SendToBrokerAdapterSink<T extends TransportProtocol> implements IAdapterPipelineElement {

  protected AdapterDescription adapterDescription;
  protected SpDataFormatDefinition dataFormatDefinition;
  protected T protocol;
  private Class<T> protocolClass;
  private EventProducer<T> producer;

  public SendToBrokerAdapterSink(AdapterDescription adapterDescription,
                                 Supplier<EventProducer<T>> producerSupplier,
                                 Class<T> protocolClass) {
    this.adapterDescription = adapterDescription;
    this.producer = producerSupplier.get();
    this.protocol = protocolClass.cast(adapterDescription
            .getEventGrounding()
            .getTransportProtocol());

    if (Envs.SP_DEBUG.getValueAsBoolean()) {
      modifyProtocolForDebugging();
    }

    TransportFormat transportFormat = adapterDescription
            .getEventGrounding()
            .getTransportFormats()
            .get(0);

    this.dataFormatDefinition =
            new TransportFormatSelector(transportFormat).getDataFormatDefinition();

    try {
      producer.connect(protocol);
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Map<String, Object> process(Map<String, Object> event) {
    try {
      if (event != null) {
        if ("true".equals(System.getenv("SP_DEBUG_CONNECT"))) {
          event.put("internal_t2", System.currentTimeMillis());
        }
        sendToBroker(dataFormatDefinition.fromMap(event));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  protected void sendToBroker(byte[] event) throws Exception {
    producer.publish(event);
  }

  protected void modifyProtocolForDebugging() {

  }

  public void changeTransportProtocol(T transportProtocol) {
    try {
      producer.disconnect();
      producer.connect(transportProtocol);
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

}


