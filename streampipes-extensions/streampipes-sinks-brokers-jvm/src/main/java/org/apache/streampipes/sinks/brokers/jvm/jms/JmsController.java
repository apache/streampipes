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


import org.apache.streampipes.model.DataSinkType;
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

public class JmsController extends StandaloneEventSinkDeclarer<JmsParameters> {

  private static final String JMS_BROKER_SETTINGS_KEY = "broker-settings";
  private static final String TOPIC_KEY = "topic";
  private static final String HOST_KEY = "host";
  private static final String PORT_KEY = "port";

//  private static final String JMS_HOST_URI = "http://schema.org/jmsHost";
//  private static final String JMS_PORT_URI = "http://schema.org/jmsPort";

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
//            .requiredOntologyConcept(Labels.withId(JMS_BROKER_SETTINGS_KEY),
//                    OntologyProperties.mandatory(JMS_HOST_URI),
//                    OntologyProperties.mandatory(JMS_PORT_URI))
        .build();
  }

  @Override
  public ConfiguredEventSink<JmsParameters> onInvocation(DataSinkInvocation graph,
                                                         DataSinkParameterExtractor extractor) {

    String topic = extractor.singleValueParameter(TOPIC_KEY, String.class);

    String jmsHost = extractor.singleValueParameter(HOST_KEY, String.class);
    Integer jmsPort = extractor.singleValueParameter(PORT_KEY, Integer.class);
//    String jmsHost = extractor.supportedOntologyPropertyValue(JMS_BROKER_SETTINGS_KEY, JMS_HOST_URI,
//            String.class);
//    Integer jmsPort = extractor.supportedOntologyPropertyValue(JMS_BROKER_SETTINGS_KEY, JMS_PORT_URI,
//            Integer.class);

    JmsParameters params = new JmsParameters(graph, jmsHost, jmsPort, topic);

    return new ConfiguredEventSink<>(params, JmsPublisher::new);
  }
}
