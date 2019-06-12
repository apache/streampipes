/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sinks.brokers.jvm.jms;


import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.helpers.OntologyProperties;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class JmsController extends StandaloneEventSinkDeclarer<JmsParameters> {

  private static final String JMS_BROKER_SETTINGS_KEY = "broker-settings";
  private static final String TOPIC_KEY = "topic";

  private static final String JMS_HOST_URI = "http://schema.org/jmsHost";
  private static final String JMS_PORT_URI = "http://schema.org/jmsPort";

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.streampipes.sinks.brokers.jvm.jms")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .requiredTextParameter(Labels.withId(TOPIC_KEY), false, false)
            .requiredOntologyConcept(Labels.withId(JMS_BROKER_SETTINGS_KEY),
                    OntologyProperties.mandatory(JMS_HOST_URI),
                    OntologyProperties.mandatory(JMS_PORT_URI))
            .build();
  }

  @Override
  public ConfiguredEventSink<JmsParameters> onInvocation(DataSinkInvocation graph, DataSinkParameterExtractor extractor) {

    String topic = extractor.singleValueParameter(TOPIC_KEY, String.class);

    String jmsHost = extractor.supportedOntologyPropertyValue(JMS_BROKER_SETTINGS_KEY, JMS_HOST_URI,
            String.class);
    Integer jmsPort = extractor.supportedOntologyPropertyValue(JMS_BROKER_SETTINGS_KEY, JMS_PORT_URI,
            Integer.class);

    JmsParameters params = new JmsParameters(graph, jmsHost, jmsPort, topic);

    return new ConfiguredEventSink<>(params, JmsPublisher::new);
  }
}
