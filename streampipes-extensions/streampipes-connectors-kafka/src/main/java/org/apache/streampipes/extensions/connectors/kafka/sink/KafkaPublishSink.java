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

package org.apache.streampipes.extensions.connectors.kafka.sink;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigExtractor;
import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider;
import org.apache.streampipes.messaging.kafka.SpKafkaProducer;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class KafkaPublishSink implements IStreamPipesDataSink {

  public static final String ID = "org.apache.streampipes.sinks.brokers.jvm.kafka";
  private static final Logger LOG = LoggerFactory.getLogger(KafkaPublishSink.class);

  private SpKafkaProducer producer;

  private JsonDataFormatDefinition dataFormatDefinition;

  public KafkaPublishSink() {
  }

  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        KafkaPublishSink::new,
        DataSinkBuilder.create(ID, 2)
            .category(DataSinkType.MESSAGING)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredProperty(EpRequirements.anyProperty())
                .build())

            .requiredTextParameter(Labels.withId(KafkaConfigProvider.TOPIC_KEY), false, false)
            .requiredTextParameter(Labels.withId(KafkaConfigProvider.HOST_KEY), false, false)
            .requiredIntegerParameter(Labels.withId(KafkaConfigProvider.PORT_KEY), 9092)

            .requiredAlternatives(Labels.withId(KafkaConfigProvider.ACCESS_MODE),
                KafkaConfigProvider.getAlternativeUnauthenticatedPlain(),
                KafkaConfigProvider.getAlternativeUnauthenticatedSSL(),
                KafkaConfigProvider.getAlternativesSaslPlain(),
                KafkaConfigProvider.getAlternativesSaslSSL())
            .requiredCodeblock(Labels.withId(
                    KafkaConfigProvider.ADDITIONAL_PROPERTIES),
                CodeLanguage.None,
                "# key=value, comments are ignored"
            )
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataSinkParameters parameters,
                                EventSinkRuntimeContext runtimeContext) {
    var kafkaConfig = new KafkaConfigExtractor().extractSinkConfig(parameters.extractor());
    this.dataFormatDefinition = new JsonDataFormatDefinition();

    this.producer = new SpKafkaProducer(
        kafkaConfig.getKafkaHost() + ":" + kafkaConfig.getKafkaPort(),
        kafkaConfig.getTopic(),
        kafkaConfig.getConfigAppenders());
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    try {
      Map<String, Object> rawEvent = event.getRaw();
      this.producer.publish(dataFormatDefinition.fromMap(rawEvent));
    } catch (SpRuntimeException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void onPipelineStopped() {
    this.producer.disconnect();
  }
}
