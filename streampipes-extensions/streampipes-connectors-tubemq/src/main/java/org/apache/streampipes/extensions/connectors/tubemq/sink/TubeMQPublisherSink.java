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

package org.apache.streampipes.extensions.connectors.tubemq.sink;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.dataformat.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import org.apache.inlong.tubemq.client.config.TubeClientConfig;
import org.apache.inlong.tubemq.client.exception.TubeClientException;
import org.apache.inlong.tubemq.client.factory.TubeSingleSessionFactory;
import org.apache.inlong.tubemq.client.producer.MessageProducer;
import org.apache.inlong.tubemq.client.producer.MessageSentResult;
import org.apache.inlong.tubemq.corebase.Message;

import java.util.Map;

public class TubeMQPublisherSink implements IStreamPipesDataSink {

  public static final String MASTER_HOST_AND_PORT_KEY = "tubemq-master-host-and-port";
  public static final String TOPIC_KEY = "tubemq-topic";

  private SpDataFormatDefinition spDataFormatDefinition;
  private String topic;

  private MessageProducer messageProducer;


  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        TubeMQPublisherSink::new,
        DataSinkBuilder
                .create("org.apache.streampipes.sinks.brokers.jvm.tubemq", 0)
                .category(DataSinkType.MESSAGING)
                .withLocales(Locales.EN).withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
                .requiredStream(StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
                .requiredTextParameter(Labels.withId(MASTER_HOST_AND_PORT_KEY)).requiredTextParameter(Labels.withId(TOPIC_KEY))
                .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataSinkParameters sinkParams,
                                EventSinkRuntimeContext runtimeContext) {
    final TubeMQParameters tubeMQParameters = new TubeMQParameters(sinkParams);

    spDataFormatDefinition = new JsonDataFormatDefinition();
    topic = tubeMQParameters.getTopic();

    final TubeClientConfig tubeClientConfig = new TubeClientConfig(tubeMQParameters.getMasterHostAndPort());
    try {
      messageProducer = new TubeSingleSessionFactory(tubeClientConfig).createProducer();
      messageProducer.publish(topic);
    } catch (TubeClientException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    final Map<String, Object> eventRawMap = event.getRaw();
    final byte[] eventMessage = spDataFormatDefinition.fromMap(eventRawMap);
    final Message tubemqMessage = new Message(topic, eventMessage);

    try {
      final MessageSentResult result = messageProducer.sendMessage(tubemqMessage);
      if (!result.isSuccess()) {
        throw new SpRuntimeException(
            String.format("Failed to send message: %s, because: %s", tubemqMessage, result.getErrMsg()));
      }
    } catch (TubeClientException | InterruptedException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void onPipelineStopped() {
    try {
      messageProducer.shutdown();
    } catch (Throwable e) {
      throw new SpRuntimeException(e);
    }
  }
}
