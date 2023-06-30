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

package org.apache.streampipes.sinks.brokers.jvm.jms;


import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.messaging.jms.ActiveMQPublisher;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import java.util.Map;

public class JmsPublisherSink extends StreamPipesDataSink {

  private static final String TOPIC_KEY = "topic";
  private static final String HOST_KEY = "host";
  private static final String PORT_KEY = "port";

  private ActiveMQPublisher publisher;
  private JsonDataFormatDefinition jsonDataFormatDefinition;

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.jms")
        .category(DataSinkType.MESSAGING)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredTextParameter(Labels.withId(TOPIC_KEY), false, false)
        .requiredTextParameter(Labels.withId(HOST_KEY), false, false)
        .requiredIntegerParameter(Labels.withId(PORT_KEY), 61616)
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {

    var extractor = parameters.extractor();
    this.jsonDataFormatDefinition = new JsonDataFormatDefinition();

    String jmsHost = extractor.singleValueParameter(HOST_KEY, String.class);
    Integer jmsPort = extractor.singleValueParameter(PORT_KEY, Integer.class);
    String topic = extractor.singleValueParameter(TOPIC_KEY, String.class);

    JmsTransportProtocol jmsTransportProtocol =
        new JmsTransportProtocol(jmsHost, jmsPort, topic);

    this.publisher = new ActiveMQPublisher(jmsTransportProtocol);
    this.publisher.connect();

    if (!this.publisher.isConnected()) {
      throw new SpRuntimeException(
          "Could not connect to JMS server " + jmsHost + " on Port: " + jmsPort
              + " to topic: " + topic);
    }
  }

  @Override
  public void onEvent(Event inputEvent) throws SpRuntimeException {
    try {
      Map<String, Object> event = inputEvent.getRaw();
      this.publisher.publish(jsonDataFormatDefinition.fromMap(event));
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.publisher.disconnect();
  }
}
