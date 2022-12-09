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

package org.apache.streampipes.sinks.brokers.jvm.kafka;

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.pe.shared.config.kafka.KafkaConnectUtils;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class KafkaController extends StandaloneEventSinkDeclarer<KafkaParameters> {

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.kafka")
        .category(DataSinkType.MESSAGING)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
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
        .build();
  }

  @Override
  public ConfiguredEventSink<KafkaParameters> onInvocation(DataSinkInvocation graph,
                                                           DataSinkParameterExtractor extractor) {
    String topic = extractor.singleValueParameter(KafkaConnectUtils.TOPIC_KEY, String.class);

    String kafkaHost = extractor.singleValueParameter(KafkaConnectUtils.HOST_KEY, String.class);
    Integer kafkaPort = extractor.singleValueParameter(KafkaConnectUtils.PORT_KEY, Integer.class);
    String authentication = extractor.selectedAlternativeInternalId(KafkaConnectUtils.ACCESS_MODE);

    KafkaParameters params;
    if (authentication.equals(KafkaConnectUtils.UNAUTHENTICATED_PLAIN)) {
      params = new KafkaParameters(graph, kafkaHost, kafkaPort, topic, authentication, null, null, false);
    } else if (authentication.equals(KafkaConnectUtils.UNAUTHENTICATED_SSL)) {
      params = new KafkaParameters(graph, kafkaHost, kafkaPort, topic, authentication, null, null, true);
    } else {
      String username = extractor.singleValueParameter(KafkaConnectUtils.USERNAME_KEY, String.class);
      String password = extractor.secretValue(KafkaConnectUtils.PASSWORD_KEY);
      if (authentication.equals(KafkaConnectUtils.SASL_PLAIN)) {
        params = new KafkaParameters(graph, kafkaHost, kafkaPort, topic, authentication, username, password, false);
      } else {
        params = new KafkaParameters(graph, kafkaHost, kafkaPort, topic, authentication, username, password, true);
      }
    }

    return new ConfiguredEventSink<>(params, KafkaPublisher::new);
  }


  public static String getSaslAccessKey() {
    return KafkaConnectUtils.SASL_PLAIN;
  }
}
