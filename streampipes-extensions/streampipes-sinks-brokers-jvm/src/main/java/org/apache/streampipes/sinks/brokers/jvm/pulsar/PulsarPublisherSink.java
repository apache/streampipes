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
package org.apache.streampipes.sinks.brokers.jvm.pulsar;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import com.google.common.annotations.VisibleForTesting;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.Map;

public class PulsarPublisherSink extends StreamPipesDataSink {

  public static final String TOPIC_KEY = "topic";
  public static final String PULSAR_HOST_KEY = "pulsar-host";
  public static final String PULSAR_PORT_KEY = "pulsar-port";
  private static final String PulsarScheme = "pulsar://";
  private static final String Colon = ":";
  private final ClientBuilder clientBuilder;
  private Producer<byte[]> producer;
  private PulsarClient pulsarClient;
  private SpDataFormatDefinition spDataFormatDefinition;
  private PulsarParameters params;

  public PulsarPublisherSink() {
    this.clientBuilder = PulsarClient.builder();
  }

  @VisibleForTesting
  public PulsarPublisherSink(ClientBuilder pulsarClientBuilder) {
    this.clientBuilder = pulsarClientBuilder;
  }

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.pulsar")
        .category(DataSinkType.MESSAGING)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredTextParameter(Labels.withId(PULSAR_HOST_KEY))
        .requiredIntegerParameter(Labels.withId(PULSAR_PORT_KEY), 6650)
        .requiredTextParameter(Labels.withId(TOPIC_KEY))
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    params = new PulsarParameters(parameters);

    this.spDataFormatDefinition = new JsonDataFormatDefinition();
    try {
      this.pulsarClient = clientBuilder.serviceUrl(makePulsarUrl(params.getPulsarHost(), params.getPulsarPort()))
          .build();

      this.producer = this.pulsarClient.newProducer()
          .topic(params.getTopic())
          .create();
    } catch (PulsarClientException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    Map<String, Object> rawMap = event.getRaw();
    byte[] jsonMessage = this.spDataFormatDefinition.fromMap(rawMap);

    try {
      this.producer.send(jsonMessage);
    } catch (PulsarClientException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    try {
      this.pulsarClient.close();
    } catch (PulsarClientException e) {
      throw new SpRuntimeException(e);
    }
  }

  private String makePulsarUrl(String hostname, Integer port) {
    return PulsarScheme + hostname + Colon + port;
  }
}
