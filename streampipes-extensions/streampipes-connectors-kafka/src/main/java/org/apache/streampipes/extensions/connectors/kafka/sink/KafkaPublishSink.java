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
import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConnectUtils;
import org.apache.streampipes.messaging.kafka.SpKafkaProducer;
import org.apache.streampipes.messaging.kafka.security.KafkaSecurityConfig;
import org.apache.streampipes.messaging.kafka.security.KafkaSecuritySaslPlainConfig;
import org.apache.streampipes.messaging.kafka.security.KafkaSecuritySaslSSLConfig;
import org.apache.streampipes.messaging.kafka.security.KafkaSecurityUnauthenticatedPlainConfig;
import org.apache.streampipes.messaging.kafka.security.KafkaSecurityUnauthenticatedSSLConfig;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import java.util.List;
import java.util.Map;

public class KafkaPublishSink implements IStreamPipesDataSink {

  private SpKafkaProducer producer;

  private JsonDataFormatDefinition dataFormatDefinition;

  private KafkaParameters params;

  public KafkaPublishSink() {
  }

  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        KafkaPublishSink::new,
        DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.kafka", 0)
            .category(DataSinkType.MESSAGING)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredProperty(EpRequirements.anyProperty())
                .build())

            .requiredTextParameter(Labels.withId(KafkaConnectUtils.TOPIC_KEY), false, false)
            .requiredTextParameter(Labels.withId(KafkaConnectUtils.HOST_KEY), false, false)
            .requiredIntegerParameter(Labels.withId(KafkaConnectUtils.PORT_KEY), 9092)

            .requiredAlternatives(Labels.withId(KafkaConnectUtils.ACCESS_MODE),
                KafkaConnectUtils.getAlternativeUnauthenticatedPlain(),
                KafkaConnectUtils.getAlternativeUnauthenticatedSSL(),
                KafkaConnectUtils.getAlternativesSaslPlain(),
                KafkaConnectUtils.getAlternativesSaslSSL())
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataSinkParameters parameters,
                                EventSinkRuntimeContext runtimeContext) {
    this.params = new KafkaParameters(parameters);
    this.dataFormatDefinition = new JsonDataFormatDefinition();

    KafkaSecurityConfig securityConfig;
    // check if a user for the authentication is defined
    if (params.useAuthentication()) {
      securityConfig = params.isUseSSL()
          ? new KafkaSecuritySaslSSLConfig(params.getUsername(), params.getPassword()) :
          new KafkaSecuritySaslPlainConfig(params.getUsername(), params.getPassword());
    } else {
      // set security config for none authenticated access
      securityConfig = params.isUseSSL()
          ? new KafkaSecurityUnauthenticatedSSLConfig() :
          new KafkaSecurityUnauthenticatedPlainConfig();
    }

    this.producer = new SpKafkaProducer(
        params.getKafkaHost() + ":" + params.getKafkaPort(),
        params.getTopic(),
        List.of(securityConfig));
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    try {
      Map<String, Object> rawEvent = event.getRaw();
      this.producer.publish(dataFormatDefinition.fromMap(rawEvent));
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onPipelineStopped() {
    this.producer.disconnect();
  }
}
