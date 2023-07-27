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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.extensions.api.monitoring.SpMonitoringManager;
import org.apache.streampipes.extensions.management.connect.adapter.util.TransportFormatSelector;
import org.apache.streampipes.extensions.management.monitoring.ExtensionsLogger;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.Map;

public abstract class SendToBrokerAdapterSink<T extends TransportProtocol> implements IAdapterPipelineElement {

  protected AdapterDescription adapterDescription;
  protected SpDataFormatDefinition dataFormatDefinition;
  protected T protocol;
  private final EventProducer producer;

  public SendToBrokerAdapterSink(AdapterDescription adapterDescription,
                                 Class<T> protocolClass) {
    this.adapterDescription = adapterDescription;
    this.protocol = protocolClass.cast(adapterDescription
        .getEventGrounding()
        .getTransportProtocol());

    if (getEnvironment().getSpDebug().getValueOrDefault()) {
      modifyProtocolForDebugging(this.protocol);
    }

    this.producer = makeProducer(this.protocol);

    TransportFormat transportFormat = adapterDescription
        .getEventGrounding()
        .getTransportFormats()
        .get(0);

    this.dataFormatDefinition =
        new TransportFormatSelector(transportFormat).getDataFormatDefinition();

    try {
      producer.connect();
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Map<String, Object> process(Map<String, Object> event) {
    try {
      if (event != null) {
        sendToBroker(dataFormatDefinition.fromMap(event));
        SpMonitoringManager.INSTANCE.increaseOutCounter(
            adapterDescription.getElementId(),
            System.currentTimeMillis());
      }
    } catch (RuntimeException e) {
      new ExtensionsLogger(adapterDescription.getElementId()).error(e);
    }
    return null;
  }

  protected void sendToBroker(byte[] event) throws RuntimeException {
    producer.publish(event);
  }

  protected abstract EventProducer makeProducer(T protocol);

  public abstract void modifyProtocolForDebugging(T protocol);

  private Environment getEnvironment() {
    return Environments.getEnvironment();
  }

}


