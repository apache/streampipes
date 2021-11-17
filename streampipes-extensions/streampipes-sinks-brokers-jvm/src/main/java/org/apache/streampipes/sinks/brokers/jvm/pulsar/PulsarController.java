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

import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class PulsarController extends StandaloneEventSinkDeclarer<PulsarParameters> {

  private static final String TOPIC_KEY = "topic";
  private static final String PULSAR_HOST_KEY = "pulsar-host";
  private static final String PULSAR_PORT_KEY = "pulsar-port";

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.pulsar")
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
  public ConfiguredEventSink<PulsarParameters> onInvocation(DataSinkInvocation graph,
                                                          DataSinkParameterExtractor extractor) {
    String pulsarHost = extractor.singleValueParameter(PULSAR_HOST_KEY, String.class);
    Integer pulsarPort = extractor.singleValueParameter(PULSAR_PORT_KEY, Integer.class);
    String topic = extractor.singleValueParameter(TOPIC_KEY, String.class);

    PulsarParameters params = new PulsarParameters(graph, pulsarHost, pulsarPort, topic);

    return new ConfiguredEventSink<>(params, Pulsar::new);
  }


}
